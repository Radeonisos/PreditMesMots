package com.entreprise.davfou.preditmesmots

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}
