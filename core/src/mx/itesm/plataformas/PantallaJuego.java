package mx.itesm.plataformas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
/**
 * Pantalla principal del juego, muestra un mapa y el personaje
 *
 * @author Roberto Martínez Román
 */
public class PantallaJuego implements Screen
{
    // Referencia al objeto de tipo Game (tiene setScreen para cambiar de pantalla)
    private Plataforma plataforma;

    // La cámara y vista principal
    private OrthographicCamera camara;
    private Viewport vista;
    // Objeto para dibujar en la pantalla
    private SpriteBatch batch;

    // MAPA
    private TiledMap mapa;      // Información del mapa en memoria
    private OrthogonalTiledMapRenderer rendererMapa;    // Objeto para dibujar el mapa

    // Personaje
    private Texture texturaPersonaje;       // Aquí cargamos la imagen marioSprite.png con varios frames
    private Personaje mario;
    public static final int TAM_CELDA = 16;

    public PantallaJuego(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    @Override
    public void show() {
        // Crea la cámara/vista
        camara = new OrthographicCamera(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA);
        camara.position.set(Plataforma.ANCHO_CAMARA / 2, Plataforma.ALTO_CAMARA / 2, 0);
        camara.update();
        vista = new StretchViewport(Plataforma.ANCHO_CAMARA, Plataforma.ALTO_CAMARA, camara);

        batch = new SpriteBatch();

        cargarRecursos();
        crearObjetos();
    }

    // Carga los recursos a través del administrador de assets
    private void cargarRecursos() {
        // Cargar las texturas/mapas
        AssetManager assetManager = plataforma.getAssetManager();   // Referencia al assetManager
        assetManager.load("Mapa.tmx", TiledMap.class);  // Cargar info del mapa
        assetManager.load("marioSprite.png", Texture.class);    // Cargar imagen

        // Se bloquea hasta que cargue todos los recursos
        assetManager.finishLoading();
    }

    private void crearObjetos() {
        // Carga el mapa en memoria
        mapa = plataforma.getAssetManager().get("Mapa.tmx");
        // Crear el objeto que dibujará el mapa
        rendererMapa = new OrthogonalTiledMapRenderer(mapa,batch);
        // Cargar frames
        texturaPersonaje = plataforma.getAssetManager().get("marioSprite.png");
        mario = new Personaje(texturaPersonaje);
        // Posición inicial del personaje
        mario.getSprite().setPosition(Plataforma.ANCHO_CAMARA / 10, Plataforma.ALTO_CAMARA * 0.90f);
    }

    /*
    Dibuja TODOS los elementos del juego en la pantalla
     */
    @Override
    public void render(float delta) {   // delta es el tiempo entre frames
        // Leer entrada

        // Actualizar objetos en la pantalla
        //mario.caer(); // Ahora depende del mapa
        moverPersonaje();

        // Dibujar
        borrarPantalla();

        batch.setProjectionMatrix(camara.combined);

        rendererMapa.setView(camara);
        rendererMapa.render();  // Dibuja el mapa

        // Entre begin/end dibujamos nuestros objetos en pantalla
        batch.begin();

        mario.render(batch);    // Dibuja el personaje

        batch.end();
    }

    /*
    Mueve el personaje en Y hasta que se encuentre sobre un bloque
     */
    private void moverPersonaje() {
        // Los bloques en el mapa son de 16x16
        // Calcula la celda donde estaría después de moverlo
        int celdaX = (int)(mario.getX()/ TAM_CELDA);
        int celdaY = (int)((mario.getY()+mario.VELOCIDAD_Y)/ TAM_CELDA);
        // Recuperamos la celda en esta posición
        // La capa 0 es el fondo
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(1);
        TiledMapTileLayer.Cell celda = capa.getCell(celdaX, celdaY);
        // probar si la celda está ocupada
        if (celda==null) {
            // Celda vacía, entonces el personaje puede avazar
            mario.caer();
        } else {
            // Dejarlo sobre la celda que lo detiene
            mario.setPosicion(mario.getX(), (celdaY+1)* TAM_CELDA);
        }
    }

    private void borrarPantalla() {
        Gdx.gl.glClearColor(0, 0, 0, 1);    // Color de fondo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        vista.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    // Libera los assets
    @Override
    public void dispose() {
        texturaPersonaje.dispose();
        mapa.dispose();
    }
}