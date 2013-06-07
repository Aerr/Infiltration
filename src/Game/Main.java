package Game;

import Game.ObjectsHandler;
import Game.Basics.Camera;

import org.newdawn.slick.*;

public class Main extends BasicGame
{

	public enum GameState
	{
		inGame, inEditor,
	}

	private GameState state;
	private static AppGameContainer app;
	private ObjectsHandler objects;
	private double bt;
	private int mouseX, mouseY;
	private int width, height;
	private Camera cam;
	private Image img_light;
	private Image img_wall;

	public Main()
	{
		super("Infiltration");
	}

	private void initEditor(GameContainer gc) throws SlickException
	{
		init(gc);
		app.setMouseGrabbed(false);
		state = GameState.inEditor;
	}

	private void initGame(GameContainer gc) throws SlickException
	{
		app.setMouseGrabbed(false);
		state = GameState.inGame;

		Image img_perso = null;
		Image img_floor = null;
		try
		{
			img_perso = new Image("images/animations.png");
			img_floor = new Image("images/floor.jpg");
			img_wall = new Image("images/wall.jpg");
			img_light = new Image("images/light.png");
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}

		objects = new ObjectsHandler(width, height, img_perso, img_floor, img_wall, img_light);
		cam = new Camera(width / 2, height / 2);
	}

	@Override
	public void init(GameContainer gc) throws SlickException
	{
		width = gc.getWidth();
		height = gc.getHeight();
		bt = 1.0f;

		// time between updates
		gc.setMinimumLogicUpdateInterval(15);
		gc.setMaximumLogicUpdateInterval(15);

		initGame(gc);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException
	{
		Input ip = gc.getInput();
		mouseX = ip.getMouseX();
		mouseY = ip.getMouseY();

		cam.update(objects.GetPlayerPos());

		objects.update(bt * (double) delta / 1000f, mouseX, mouseY, ip, cam.getPos());

		// Press tab to restart
		if (ip.isKeyPressed(Input.KEY_TAB))
		{
			// bt = 1.0f;
			init(gc);
		}
		if (ip.isKeyPressed(Input.KEY_F1))
		{
			switch (state)
			{
			case inGame:
				initEditor(gc);
				objects.setInEditor(true);
				break;
			case inEditor:
				initGame(gc);
				objects.setInEditor(false);
				break;
			default:
				initGame(gc);
				objects.setInEditor(false);
				break;
			}
		}

		// Press ESC to quit
		if (ip.isKeyPressed(Input.KEY_ESCAPE))
		{
			System.exit(0);
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor(Color.white);
		g.drawString(String.format("FPS : %d", gc.getFPS()), 0, 0);

		g.setLineWidth(2);
		g.setBackground(Color.gray);
		g.pushTransform();
		g.translate(-(float) cam.getPos().X, -(float) cam.getPos().Y);

		objects.render(gc, g, state, mouseX + (float) cam.getPos().X, mouseY + (float) cam.getPos().Y);

		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor(Color.white);
//		g.drawString(String.format("FPS : %d", gc.getFPS()), 0, 0);
		g.flush();
	}

	public static void main(String[] args) throws SlickException
	{
		app = new AppGameContainer(new Main());
		app.setDisplayMode(1920, 1080, false);
		app.setShowFPS(false);
		app.start();
	}
}