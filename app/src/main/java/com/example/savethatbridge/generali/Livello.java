package com.example.savethatbridge.generali;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.savethatbridge.R;
import com.example.savethatbridge.gameobjects.Aggancio;
import com.example.savethatbridge.gameobjects.Bomba;
import com.example.savethatbridge.gameobjects.Collina;
import com.example.savethatbridge.gameobjects.GameObject;
import com.example.savethatbridge.gameobjects.GameWorld;
import com.example.savethatbridge.gameobjects.Interfaccia;
import com.example.savethatbridge.gameobjects.Ponte;
import com.example.savethatbridge.gameobjects.Terrorista;


import java.util.ArrayList;

public class Livello {

    private static Box physicalSize;
    private float lunghezzaPonte;
    private final float altezzaAggancio;
    private Context context;
    private MediaPlayer mediaPlayer;

    enum Livelli{
        Livello_1,
        Livello_2,
        End
    }
    public Livello(Context context,GameWorld world)
    {
        physicalSize = world.getPhysicalSize();
        this.lunghezzaPonte = GameWorld.getLunghezzaPonte();
        this.altezzaAggancio=GameWorld.getAltezzaAggancio();
        this.context=context;
    }

    public void level1(GameWorld world)
    {
        //Setup del background del livello
        setupIniziale(world,Livelli.Livello_1);
        //Aggiunta interfaccia
        aggiuntaInterfaccia(world);
        //Aggunta agganci di rafforzamento
        aggiuntaAgganci(world,1);
        //Aggiunta delle estremità della strada
        aggiuntaColline(world, 2);
        //Aggiunta ponte con i suoi agganci
        aggiuntaPonte(world,5);
        //aggiuntaJoint
        aggiuntaJoint(world,2,5);
        //Aggiunta terrorista
        aggiuntaTerrorista(world);
        //Aggiunta bomba
        aggiuntaBomba(world,1,5);
        //Definizione del quantitativo di travi a disposizione
        GameWorld.setNumeroTravi(1);
        Interfaccia.setContatoreTravi(1);
        GameWorld.trave = new ArrayList<>(GameWorld.getNumeroTravi());
    }

    public void level2(GameWorld world)
    {
        //Setup del background del livello
        setupIniziale(world,Livelli.Livello_2);
        //Aggiunta interfaccia
        aggiuntaInterfaccia(world);
        //Aggunta agganci di rafforzamento
        aggiuntaAgganci(world,1);
        //Aggiunta delle estremità della strada
        aggiuntaColline(world, 2);
        //Aggiunta ponte con i suoi agganci
        aggiuntaPonte(world,5);
        //aggiuntaJoint
        aggiuntaJoint(world,2,5);
        //Aggiunta terrorista
        aggiuntaTerrorista(world);
        //Aggiunta bomba
        aggiuntaBomba(world,2,5);

        //Definizione del quantitativo di travi a disposizione
        GameWorld.setNumeroTravi(2);
        Interfaccia.setContatoreTravi(2);
        GameWorld.trave = new ArrayList<>(GameWorld.getNumeroTravi());
        GameWorld.trave.clear();
    }

    public void endLevel(GameWorld world){
        setupIniziale(world, Livelli.End);
        //Aggiunta sound
        aggiungiCaratteristiche(world);
    }

    private void aggiungiCaratteristiche(GameWorld world) {
        mediaPlayer=MediaPlayer.create(this.context,R.raw.applausi);
        mediaPlayer.start();
    }

    /**Setup del background del livello**/
    private void setupIniziale(GameWorld world, Livelli livello)
    {
        //BitmapFactory crea oggetti Bitmap da varie sorgenti (file, stream ...)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        switch (livello) {
            case Livello_1:
                world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.background_livello1, options));
                break;
            case Livello_2:
                world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.background_livello2, options));
                break;
            case End:
                world.setBitmap(BitmapFactory.decodeResource(world.getActivity().getResources(), R.drawable.end_background, options));
                break;
            default:
                throw new IllegalArgumentException("Livello invalido: " + livello);
        }

        GameWorld.setCostruire(true);
        GameWorld.setBordo(world.aggiungiOggetto(new EnclosureGO(world, physicalSize.getxMin(), physicalSize.getxMax(), physicalSize.getyMin(), physicalSize.getyMax())));
    }

    /**Aggiunta interfaccia con i suoi ocntatori**/
    private void aggiuntaInterfaccia(GameWorld world) {
        Interfaccia interfaccia= new Interfaccia(world);
        interfaccia.setContatoreLivello(GameWorld.getLivello());
        interfaccia.setContatoreTravi(GameWorld.getNumeroTravi());
        if(GameWorld.getLivello()==1)
            interfaccia.setTimer(-1);
        else
            interfaccia.setTimer(10);
        world.aggiungiOggetto(interfaccia);
    }

    /**Aggiunta degli agganci di rafforzamento**/
    private void aggiuntaAgganci(GameWorld gameWorld, int numeroAgganci)
    {
        ArrayList<GameObject> agganci = new ArrayList<>(numeroAgganci);
        if(numeroAgganci==2){
            agganci.add(gameWorld.aggiungiOggetto(new Aggancio(gameWorld, (-this.lunghezzaPonte / 2)+0.5f , (this.physicalSize.getyMax()/2)-1.8f)));
            agganci.add(gameWorld.aggiungiOggetto(new Aggancio(gameWorld, (-this.lunghezzaPonte / 2)+0.5f , (this.physicalSize.getyMax()/2)+1.8f)));
        }
        agganci.add(gameWorld.aggiungiOggetto(new Aggancio(gameWorld, (-this.lunghezzaPonte / 2)+0.5f , (this.physicalSize.getyMax()/2)-1.8f)));
        GameWorld.agganci=agganci;
    }

    /**Aggiunta delle colline e i suoi agganci**/
    private void aggiuntaColline(GameWorld gameWorld, int agganci) {
        ArrayList<GameObject> agganciColline = new ArrayList<>(agganci);
        agganciColline.add(gameWorld.aggiungiOggetto(new Collina(gameWorld, physicalSize.getxMin(), -this.lunghezzaPonte / 2, 0 , physicalSize.getyMax())));
        agganciColline.add(gameWorld.aggiungiOggetto(new Collina(gameWorld, this.lunghezzaPonte / 2, physicalSize.getxMax(), 0, physicalSize.getyMax())));
        GameWorld.colline = agganciColline;
    }

    /**Aggiunta del ponte con i suoi agganci**/
    private void aggiuntaPonte(GameWorld gameWorld, int numeroAgganci){
        float larghezzaAggancio= lunghezzaPonte/numeroAgganci;
        GameWorld.ponte=new ArrayList<GameObject>(numeroAgganci);
        for (int i = 0; i < numeroAgganci; i++) {
            GameObject ponte = new Ponte(gameWorld, (-this.lunghezzaPonte/2)+(i*numeroAgganci), 0, larghezzaAggancio, altezzaAggancio);
            GameWorld.ponte.add(gameWorld.aggiungiOggetto(ponte));
        }
    }
    /**Aggiunta Revolution Joints tra agganci**/
     private void aggiuntaJoint(GameWorld gameWorld, int anchors, int numeroAgganci){
     float larghezzaGancio= lunghezzaPonte/numeroAgganci;
     //Definizione di un'ArrayList di numeroAgganci+anchors elementi
     GameWorld.joint = new ArrayList<>(anchors+numeroAgganci);
     GameWorld.joint.clear();
     //Creazione joint tra la 1° collina e il primo aggancio del ponte
     MyRevoluteJoint joint = new MyRevoluteJoint(gameWorld, GameWorld.colline.get(0).body, GameWorld.ponte.get(0).body, -larghezzaGancio / 2, -altezzaAggancio / 2, (((Collina) GameWorld.colline.get(0)).getWidth() / 2), -(((Collina) GameWorld.colline.get(0)).getHeight()/2));
     GameWorld.joint.add(joint);
     //Creazione joint tra i vari agganci del ponte
     for (int i = 0; i < numeroAgganci - 1; i++) {
     joint = new MyRevoluteJoint(gameWorld, GameWorld.ponte.get(i).body, GameWorld.ponte.get(i+1).body, -larghezzaGancio / 2, -altezzaAggancio / 2, larghezzaGancio / 2, -altezzaAggancio / 2);
     GameWorld.joint.add(joint);
     }
     //Creazione joint tra l'ultimo aggancio del ponte e la 2° collina
     joint = new MyRevoluteJoint(gameWorld, GameWorld.ponte.get(numeroAgganci-1).body, GameWorld.colline.get(1).body, -((Collina) GameWorld.colline.get(0)).getWidth() / 2, -(((Collina) GameWorld.colline.get(0)).getHeight() / 2), larghezzaGancio / 2, -altezzaAggancio / 2);
     GameWorld.joint.add(joint);
     }

    /**Aggiunta del terrorista**/
    private void aggiuntaTerrorista(GameWorld gameWorld){
        Terrorista terrorista = new Terrorista(context,gameWorld, physicalSize.getxMin()+1, -1f);
        gameWorld.aggiungiOggetto(terrorista);
        GameWorld.setTerrorista(terrorista);
    }

    /**Aggiunta della bomba**/
    private void aggiuntaBomba(GameWorld gameWorld, int numeroBombe, int numeroAgganci)
    {
        float larghezzaAggancio= lunghezzaPonte/numeroAgganci;
        MyRevoluteJoint joint;
        if(numeroBombe==1) {
            joint = GameWorld.joint.get(3);
            Bomba bomba = new Bomba(this.context, gameWorld, joint.getJoint().getBodyB().getPositionX() - larghezzaAggancio / 2, (joint.getJoint().getBodyB().getPositionY() + altezzaAggancio / 2), joint);
            GameWorld.aggiungiBomba(bomba);
        }
        else{
            joint=GameWorld.joint.get(1);
            Bomba bomba = new Bomba(this.context, gameWorld, joint.getJoint().getBodyB().getPositionX() - larghezzaAggancio / 2, (joint.getJoint().getBodyB().getPositionY() + altezzaAggancio / 2), joint);
            GameWorld.aggiungiBomba(bomba);
            joint = GameWorld.joint.get(2);
            bomba = new Bomba(this.context, gameWorld, joint.getJoint().getBodyB().getPositionX() - larghezzaAggancio / 2, (joint.getJoint().getBodyB().getPositionY() + altezzaAggancio / 2), joint);
            GameWorld.aggiungiBomba(bomba);
        }
    }
}
