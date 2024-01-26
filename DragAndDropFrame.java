import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

public class DragAndDropFrame extends JFrame implements MouseListener, MouseMotionListener{
	
	private static Dimension frameDimension = new Dimension(600, 600);
	
	//since the frame and mouse event have different coordinate system, offset adjustment is needed
	private static final Vectors.Vector2Int mouseCoordSystemOffset = new Vectors.Vector2Int(7, 30);
	
	private RectanglePanel draggablePanel;
	
	//switch to determine whether a mouse drag event would move the draggable panel
	private boolean onDrag = false;
	
	//track the last mouse pressed / drag position to calc. the mouse movement -> draggable panel movement
	private Vectors.Vector2Int lastMouseDragCoord;
	
	public DragAndDropFrame(){
		
		//setting the frame dimension through modifying its content pane
		this.getContentPane().setPreferredSize(frameDimension);
		this.pack();

		//configuring additional properties of the frame
		this.getContentPane().setBackground(Color.black);
		this.setLayout(null);
		
		//creating a draggable panel with centered position
		this.draggablePanel = new RectanglePanel();
		this.CenterPanel(this.draggablePanel);
		this.add(this.draggablePanel);
		
		//incorporating mouse events to the JFrame, allowing actions corresponding to mouse inputs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setVisible(true);
	}
	
	public void CenterPanel(JPanel panelObject) {
		
		/*centering a panel by defining frame's center coordinate, substracted
		 * by the local center coord of panel object to make the panel pivot centered*/
		int panelWidth = panelObject.getWidth();
		int panelHeight = panelObject.getHeight();
		
		int parentWidth = (int)frameDimension.getWidth();
		int parentHeight = (int)frameDimension.getHeight();
		
		int centeredXCoord = (parentWidth - panelWidth) / 2;
		int centeredYCoord = (parentHeight - panelHeight) / 2;
		
		Rectangle centeredBounds = new Rectangle(centeredXCoord, centeredYCoord, panelWidth, panelHeight);
		panelObject.setBounds(centeredBounds);
	}
	
	//evaluating whether a certain coordinate is within the draggable panel's bounds
	public boolean coordWithinDragablePanelBounds(Vectors.Vector2Int vector2Int) {
		
		int x = vector2Int.x;
		int y = vector2Int.y;
		
		Rectangle dragablePanelBounds = this.draggablePanel.getBounds();
		
		if (x > dragablePanelBounds.getX() 
			&& x < dragablePanelBounds.getX() + dragablePanelBounds.getWidth()
			&& y > dragablePanelBounds.getY()
			&& y < dragablePanelBounds.getY() + dragablePanelBounds.getHeight()) {
			return true;
		}
		return false;
	}
	
	//moving the panel object with movement specifiec by 2D vector
	/*This will be invoked whenever a mouse is being dragged AND the onDrag switch is turned on.
	 * the reason the parameter is mouse movement / delta position instead of mouse absolute position:
	 * absolute pos. would result in immediate movement jump in some occasion, while mouse movement
	 * or delta pos. would result in smooth draggable panel movement, reflecting the mouse movement*/
	public void movePanel(JPanel panelObject, Vectors.Vector2Int movement) {
		Rectangle currentBounds = panelObject.getBounds();
		
		int originalX = (int)currentBounds.getX();
		int originalY = (int)currentBounds.getY();
		
		/*if one of the movement component causes the panel object to be outside of the 
		 * frame boundary, cancel that movement component (x or y)*/
		if (coordOutsideFrame(new Vectors.Vector2Int(originalX + movement.x, originalY))) {
			movement.x = 0;
		}
		if (coordOutsideFrame(new Vectors.Vector2Int(originalX, originalY + movement.y))) {
			movement.y = 0;
		}
		
		int translatedX = originalX + movement.x;
		int translatedY = originalY + movement.y;
		
		int panelWidth = (int)currentBounds.getWidth();
		int panelHeight = (int)currentBounds.getHeight();
		
		//setting the panel bounds with the new translated bounds
		Rectangle newPanelBounds = new Rectangle(translatedX, translatedY, panelWidth, panelHeight);
		panelObject.setBounds(newPanelBounds);
	}
	
	//evaluating whether given coordinate is within the frame's bound
	public boolean coordOutsideFrame(Vectors.Vector2Int coord) {
		int xOffset = this.draggablePanel.getWidth();
		int yOffset = this.draggablePanel.getHeight();
		
		int xLowerBound = 0;
		int xUpperBound = frameDimension.width - xOffset;
		int yLowerBound = 0;
		int yUpperBound = frameDimension.height - yOffset;
		if (coord.x >= xLowerBound && coord.x <= xUpperBound &&
			coord.y >= yLowerBound && coord.y <= yUpperBound) {
			return false; 
		}
		return true;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	/*pressing mouse indicates an attempt to drag the object. it first will check whether the mouse
	 * is pressed within the draggable panel boundary. if it is, then proceed to drag the panel
	 * by setting the boolean switch onDrag to true*/
	@Override
	public void mousePressed(MouseEvent e) {
		//getting the press position
		Vectors.Vector2Int mousePressedCoord = new Vectors.Vector2Int(e.getX(), e.getY());
		
		//adjusting the press position by offset due to coord. system difference
		mousePressedCoord = Vectors.Vector2Int.Substract(mousePressedCoord, mouseCoordSystemOffset);
		
		//check whether the coord falls within the draggable panel bounds
		if (coordWithinDragablePanelBounds(mousePressedCoord)) {
			this.lastMouseDragCoord = mousePressedCoord;
			onDrag = true;
		}
	}
	
	//when releasing a mouse, if the mouse previously is dragging, stop the drag
	@Override
	public void mouseReleased(MouseEvent e) {
		if (onDrag) {
			onDrag = false;
			this.lastMouseDragCoord = null;
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		//drag the mouse if the initial mouse press landed on the draggable object / within the bounds
		if (onDrag) {
			//getting the mouse drag position
			Vectors.Vector2Int mouseDraggedCoord = new Vectors.Vector2Int(e.getX(), e.getY());
			
			//adjusting the mouse drag pos. with offset due to coord. system difference
			mouseDraggedCoord = Vectors.Vector2Int.Substract(mouseDraggedCoord, mouseCoordSystemOffset);
			
			//getting the mouse movement by substracting current drag pos with previous mouse pos.
			Vectors.Vector2Int mouseMovement = Vectors.Vector2Int.Substract(mouseDraggedCoord, this.lastMouseDragCoord);
			
			//move panel according to the mouse movement, implying the panel follows the mouse movement
			movePanel(this.draggablePanel, mouseMovement);
			
			//updating the previous mouse drag pos. with the current mouse drag pos.
			this.lastMouseDragCoord = mouseDraggedCoord;
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}

//sub JPanel, setting fixed size and color during initialization. will be a draggable panel
class RectanglePanel extends JPanel{

	public static int side = 80;
	
	public RectanglePanel() {
		this.setSize(side, side);
		this.setBackground(new Color(57, 255, 20));
	}
}
