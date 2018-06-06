package com.entreprise.davfou.preditmesmots

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    private var mDB: Database?= null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private var mUIHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()

        mDB = Database.getInstance(this)

        //test()
        fetchFromDB()


        val rootView: ViewGroup = findViewById(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r);

            val heightDiff = rootView.rootView.height - (r.bottom - r.top);
            if (heightDiff > rootView.rootView.height / 4) {
                layoutBtn.visibility= View.VISIBLE;
            }else{
                layoutBtn.visibility= View.GONE;

            }
        }

        edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {


                var lenghtText : Int =p0.toString().length

                //devoir récupérer le dernier mot rentré

                if(lenghtText>0) {
                    //On test si un espace est tapé
                    if(p0.toString().get(lenghtText-1).isWhitespace()){
                        //On doit afficher les 3 mots à prédire
                        println("space detect")
                        var ss = p0.toString().substring(0,lenghtText-1)
                        var words = ss.split(" ")
                        var test = words[words.size-1]
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })




        button1.setOnClickListener(View.OnClickListener {
            clickedOnButton1();
        })

        button2.setOnClickListener(View.OnClickListener {
            clickedOnButton2();
        })

        button3.setOnClickListener(View.OnClickListener {
            clickedOnButton3();
        })


    }

    fun clickedOnButton1(){
        var toto : String  = edittext.text.toString() + "Mot1";
        edittext.text=Editable.Factory.getInstance().newEditable(toto);
        edittext.setSelection(edittext.text.toString().length)
    }


    fun clickedOnButton2(){
        var toto : String  = edittext.text.toString() + "Mot2";
        edittext.text=Editable.Factory.getInstance().newEditable(toto);
        edittext.setSelection(edittext.text.toString().length)
    }


    fun clickedOnButton3(){
        var toto : String  = edittext.text.toString() + "Mot3";
        edittext.text=Editable.Factory.getInstance().newEditable(toto);
        edittext.setSelection(edittext.text.toString().length)
    }

    private fun fetchFromDB(){
        val task = Runnable {
            val data = mDB?.DataDAO()?.getAll()
            println("Datasize : "+data?.size)

        }
        mDbWorkerThread.postTask(task)
    }

    private fun insertInDB(data: Data){
        val task = Runnable { mDB?.DataDAO()?.insert(data) }
        mDbWorkerThread.postTask(task)
    }

    fun test() {
        var fileReader: BufferedReader? = null

        try {
            val datas = ArrayList<Data>()
            var line: String?

            //var path = Uri.parse("android.resource://com.entreprise.davfou.preditmesmots/" + R.raw.gram).path
            //println(path)

            //fileReader = BufferedReader(FileReader(path))
            fileReader = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.gram)))

            // Read CSV header
            fileReader.readLine()

            // Read the file line by line starting from the second line
            line = fileReader.readLine()
            while (line != null) {
                val tokens = line.split(",")
                if (tokens.size > 0) {
                    val data = Data(
                            null,
                            tokens[0],
                            tokens[1],
                            Integer.parseInt(tokens[2]))
                    datas.add(data)
                    insertInDB(data)
                }

                line = fileReader.readLine()
            }

            // Print the new customer list
            for (data in datas) {
                println(data)
            }
        } catch (e: Exception) {
            println("Reading CSV Error!")
            e.printStackTrace()
        } finally {
            try {
                fileReader?.close()
            } catch (e: IOException) {
                println("Closing fileReader Error!")
                e.printStackTrace()
            }
        }
    }
}
