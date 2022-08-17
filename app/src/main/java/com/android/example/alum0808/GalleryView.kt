package com.android.example.alum0808

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.gallery_view.*
import kotlinx.android.synthetic.main.view_image_item.view.*

class GalleryView: AppCompatActivity() {
    //imageUrisに可変リストを作成する
    val imageUris = mutableListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        //よくわからない、、、先にやりますということ？
        super.onCreate(savedInstanceState)
        //もどる矢印を設置する
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //gallery_viewを表示する
        setContentView(R.layout.gallery_view)
        //titleにTextViewを代入する
        val title = findViewById<TextView>(R.id.title)
        //MainActivityからalumIdを取得する
        val alumId: Long = intent.getLongExtra("id", 0)
        //DBHelperクラスを呼び出す
        val helper = DBHelper(this)
        //alumIdがロング型でないとき
        if (alumId != 0L) {
            //ヘルパークラスを使ってalumsテーブルにクエリを投げる
            helper.readableDatabase.use { db ->
                //Where select id,title from alums alums.id = alumId limit 1
                db.query("alums", arrayOf("id","title"), "id = ?", arrayOf(alumId.toString()), null, null, null, "1")
                    .use { cursor ->/* cursorは、検索結果の各行の情報にアクセスするためのオブジェクト。*/
                        //カーソル位置が最初に戻ることに成功したら
                        if (cursor.moveToFirst()) {
                            //タイトルにカーソルで取得した1番目の要素を代入(alumIdに対応するタイトルを取得)
                            title.setText(cursor.getString(1))
                        }
                    }
            }
        }

        /* applyメソッドでRecyclerViewを実行する */
        recyclerView.apply {
            //viewのサイズは固定なので、サイズを固定してパフォーマンス向上
            setHasFixedSize(true)
            //RecyclerViewのlayoutManagerをGridLayoutManagerに設定する、配列数は3
            layoutManager = GridLayoutManager(this@GalleryView, 3)
            //recyclerViewに可変リストデータ(ImageAdapter)を紐付ける
            adapter = ImageAdapter()
        }

        //cameraボタンの設定
        val camera =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.camera_start)
        camera.setOnClickListener {
            val intent = Intent(this, CameraView::class.java)
            intent.putExtra("id", alumId)
            startActivity(intent)
        }
        //deleteボタンの設定
        val delete =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.delete_alum)
        delete.setOnClickListener {
            helper.writableDatabase.use {
                    db ->
                db.delete("alums", "id = ?", arrayOf(alumId.toString()))
                Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show()
            }
            //MAinActivity画面にもどる
            finish()
        }
    }
    //Activity表示時にはじめる
    override fun onResume() {
        //よくわからない、、、先にやりますということ？
        super.onResume()
        /*
        READ_EXTERNAL_STORAGEパーミッション取得済みかどうかの確認。
        READ_EXTERNAL_STORAGEは実行時に権限を要求する必要がある。
        簡単のため、パーミッション関係の実装は正常系のみとしている。
        */
        val result = PermissionChecker.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        /* パーミッション未取得なら、要求する。 */
        if (result != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            return
        }

        /* パーミッション取得済みなら、画像を読み込む。*/
        loadImages()
    }

    //画像を読み込む
    private fun loadImages() {
        //imageUrisを削除(初期化する)
        imageUris.clear()
        //MainActivityからalumIdを取得する
        val alumId: Long = intent.getLongExtra("id", 0)
        /* 検索条件の設定。*/
         /* 読み込む列の指定。nullならすべての列を読み込む。*/
        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )
        /* 行の絞り込みの指定。nullならすべての行を読み込む。PICASA_IDは非推奨*/
        val selection = "${MediaStore.Images.Media.PICASA_ID} = ?"

        /* selectionの?を置き換える引数 SQLインジェクションのための処理*/
        val selectionArgs = arrayOf(
            alumId.toString()
        )
        /* 並び順。nullなら指定なし。*/
        val sortOrder = MediaStore.Images.Media._ID + " desc"


        //このサンプルではMediaStore以外のソースコードを極力少なくするため、メインスレッドで実行している。
        //そのため、スクロールが頻繁に固まる。実際のアプリではバックグラウンドスレッドで実行すること。

        //content配下のURIをクエリで取得する
        contentResolver.query(
            //select URI
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, sortOrder
        )?.use { cursor -> /* cursorは、検索結果の各行の情報にアクセスするためのオブジェクト。*/
            /* 必要な情報が格納されている列番号を取得する。 */
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) { /* 順にカーソルを動かしながら、情報を取得していく。*/
                val id = cursor.getLong(idColumn)
                /* IDからURIを取得してリストに格納 */
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageUris.add(uri)
            }
        }

        /* 表示更新 */
        recyclerView.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val REQUEST_CODE = 1
    }

    inner class ImageAdapter: RecyclerView.Adapter<ImageViewHolder>() {
        override fun getItemCount() = imageUris.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.view_image_item, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.itemView.imageView.setImageURI(imageUris[position])
        }
    }

    inner class ImageViewHolder(v: View): RecyclerView.ViewHolder(v)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}