package com.example.savethatbridge.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**Definizione classe delle particelle, corpi rigidi senza orientamento**/
public class Particelle extends GameObject {
    private final Canvas canvas;
    private final ParticleSystem particleSystem;
    private ParticleGroup particleGroup;
    private byte[] posizioneParticelle;
    private ByteBuffer posizioneParticelleBuffer;
    private final Paint paint = new Paint();
    private static final int PARTICLE_BYTES = 128;
    private static int BUFFER_OFFSET;
    private static boolean isLittleEndian;

    static {
        discoverEndianness();
    }

    public Particelle(GameWorld gw, int i,float x, float y) {
        super(gw);

        this.canvas = new Canvas(gw.getBitmapBuffer());
        //Ottenimento del sistema di particelle creato e istanziato nel gameworld
        this.particleSystem = gw.getParticleSystem(i);

        //Definizione della forma delle particelle: circolari di raggio 2
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(2);

        //Creazione del gruppo di particelle avente una certa shape, una posizione, di una certa consistenza e  tempo di vita
        ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.setShape(circleShape);
        particleGroupDef.setPosition(x,y);
        particleGroupDef.setGroupFlags(ParticleGroupFlag.solidParticleGroup);
        particleGroupDef.setFlags(ParticleFlag.powderParticle);
        particleGroupDef.setLifetime(3);
        this.particleGroup = this.particleSystem.createParticleGroup(particleGroupDef);


        this.posizioneParticelleBuffer = ByteBuffer.allocateDirect(this.particleGroup.getParticleCount() * PARTICLE_BYTES);
        this.posizioneParticelle = this.posizioneParticelleBuffer.array();


        //rilascio degli oggetti nativi
        circleShape.delete();
        particleGroupDef.delete();
    }

    @Override
    public void draw(Bitmap buffer, float _x, float _y, float _angle) {
        //Con una sola invocazione a LiquidFun riporta nel mondo Java le coordinate di tutte le particelle e le copia nel buffer 'posizioneParticelleBuffer'
        particleSystem.copyPositionBuffer(0, this.particleGroup.getParticleCount(), posizioneParticelleBuffer);

        paint.setARGB(255, 150, 150, 150);
        //Ciclo che gestisce le posizioni delle singole particelle tramite la loro posizione, lavorando esplicitamente sui bit
        for (int i = 0; i < this.particleGroup.getParticleCount() / 2; i++) {
            int xint, yint;
            if (isLittleEndian) {
                xint = (posizioneParticelle[i * 8 + BUFFER_OFFSET] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 1] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 2] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 3] & 0xFF) << 24;
                yint = (posizioneParticelle[i * 8 + BUFFER_OFFSET + 4] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 5] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 6] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 7] & 0xFF) << 24;
            } else {
                xint = (posizioneParticelle[i * 8] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 1] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 2] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 3] & 0xFF);
                yint = (posizioneParticelle[i * 8 + 4] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 5] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 6] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 7] & 0xFF);
            }

            float x = Float.intBitsToFloat(xint), y = Float.intBitsToFloat(yint);
            canvas.drawCircle(gw.setWorldToFrameX(x), gw.setWorldToFrameY(y), 4, paint);
        }
        paint.setARGB(255, 200, 200, 50);
        for (int i = this.particleGroup.getParticleCount() / 2; i < 4 * this.particleGroup.getParticleCount() / 6; i++) {
            int xint, yint;
            if (isLittleEndian) {
                xint = (posizioneParticelle[i * 8 + BUFFER_OFFSET] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 1] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 2] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 3] & 0xFF) << 24;
                yint = (posizioneParticelle[i * 8 + BUFFER_OFFSET + 4] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 5] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 6] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 7] & 0xFF) << 24;
            } else {
                xint = (posizioneParticelle[i * 8] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 1] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 2] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 3] & 0xFF);
                yint = (posizioneParticelle[i * 8 + 4] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 5] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 6] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 7] & 0xFF);
            }

            float x = Float.intBitsToFloat(xint), y = Float.intBitsToFloat(yint);
            canvas.drawCircle(gw.setWorldToFrameX(x), gw.setWorldToFrameY(y), 4, paint);
        }

        paint.setARGB(255, 200, 50, 50);
        for (int i = 4 * this.particleGroup.getParticleCount() / 6; i < 5 * this.particleGroup.getParticleCount() / 6; i++) {
            int xint, yint;
            if (isLittleEndian) {
                xint = (posizioneParticelle[i * 8 + BUFFER_OFFSET] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 1] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 2] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 3] & 0xFF) << 24;
                yint = (posizioneParticelle[i * 8 + BUFFER_OFFSET + 4] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 5] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 6] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 7] & 0xFF) << 24;
            } else {
                xint = (posizioneParticelle[i * 8] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 1] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 2] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 3] & 0xFF);
                yint = (posizioneParticelle[i * 8 + 4] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 5] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 6] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 7] & 0xFF);
            }

            float x = Float.intBitsToFloat(xint), y = Float.intBitsToFloat(yint);
            canvas.drawCircle(gw.setWorldToFrameX(x), gw.setWorldToFrameY(y), 4, paint);
        }

        paint.setARGB(255, 200, 150, 50);
        for (int i = 5 * this.particleGroup.getParticleCount() / 6; i < this.particleGroup.getParticleCount(); i++) {
            int xint, yint;
            if (isLittleEndian) {
                xint = (posizioneParticelle[i * 8 + BUFFER_OFFSET] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 1] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 2] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 3] & 0xFF) << 24;
                yint = (posizioneParticelle[i * 8 + BUFFER_OFFSET + 4] & 0xFF) | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 5] & 0xFF) << 8 |
                        (posizioneParticelle[i * 8 + BUFFER_OFFSET + 6] & 0xFF) << 16 | (posizioneParticelle[i * 8 + BUFFER_OFFSET + 7] & 0xFF) << 24;
            } else {
                xint = (posizioneParticelle[i * 8] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 1] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 2] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 3] & 0xFF);
                yint = (posizioneParticelle[i * 8 + 4] & 0xFF) << 24 | (posizioneParticelle[i * 8 + 5] & 0xFF) << 16 |
                        (posizioneParticelle[i * 8 + 6] & 0xFF) << 8 | (posizioneParticelle[i * 8 + 7] & 0xFF);
            }

            float x = Float.intBitsToFloat(xint), y = Float.intBitsToFloat(yint);
            //Disegno dei cerchi
            canvas.drawCircle(gw.setWorldToFrameX(x), gw.setWorldToFrameY(y), 4, paint);
        }
    }

    public static void discoverEndianness() {
        isLittleEndian = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);
        Log.d("DEBUG", "Build.FINGERPRINT=" + Build.FINGERPRINT);
        Log.d("DEBUG", "Build.PRODUCT=" + Build.PRODUCT);
        BUFFER_OFFSET = 4;
    }
}
