package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.prism.image.ViewPort;

import org.w3c.dom.Text;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private Random numRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo;
    private Rectangle canoBaixoRetangulo;

    //Configurações
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoDoJogo = 0;
    private int pontuacao = 0;

    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posMovCanoHorizontal;
    private float posMovCanoVertical;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRand;
    private boolean marcouPonto = false;

    private float variacao = 0;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;


    @Override
    public void create() {
        batch = new SpriteBatch();
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");
        numRandomico = new Random();
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);
        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        passaroCirculo = new Circle();

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;

        posicaoInicialVertical = alturaDispositivo / 2;
        posMovCanoHorizontal = larguraDispositivo;
        posMovCanoVertical = alturaDispositivo / 2;
        espacoEntreCanos = 300;

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

    }

    @Override
    public void render() {

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 8;

        if (variacao > 2) {
            variacao = 0;
        }

        if (estadoDoJogo == 0) {
            if (Gdx.input.justTouched()) {
                estadoDoJogo = 1;
            }
        } else {
            velocidadeQueda++;

            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            if (estadoDoJogo == 1) {
                posMovCanoHorizontal -= deltaTime * 200;
                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -13;
                }
                if (posMovCanoHorizontal < -canoTopo.getWidth()) {
                    posMovCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRand = numRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if (posMovCanoHorizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            } else {
                if (Gdx.input.justTouched()) {
                    estadoDoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posMovCanoHorizontal = larguraDispositivo;
                }
            }
        }

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posMovCanoHorizontal,
                posMovCanoVertical + espacoEntreCanos / 2 + alturaEntreCanosRand);
        batch.draw(canoBaixo, posMovCanoHorizontal,
                posMovCanoVertical - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRand);
        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if (estadoDoJogo == 2) {
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2,
                    alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 - 200,
                    alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth() / 2,
                posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        canoBaixoRetangulo = new Rectangle(
                posMovCanoHorizontal,
                posMovCanoVertical - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRand,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        canoTopoRetangulo = new Rectangle(
                posMovCanoHorizontal,
                posMovCanoVertical + espacoEntreCanos / 2 + alturaEntreCanosRand,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

        //teste colisão
        if (Intersector.overlaps(passaroCirculo, canoBaixoRetangulo)
                || Intersector.overlaps(passaroCirculo, canoTopoRetangulo)
                || posicaoInicialVertical <= 0
                || posicaoInicialVertical >= alturaDispositivo) {
            estadoDoJogo = 2;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
