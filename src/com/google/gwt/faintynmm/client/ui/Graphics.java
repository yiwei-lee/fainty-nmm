package com.google.gwt.faintynmm.client.ui;

import java.util.ArrayList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Graphics extends Composite implements Presenter.View {
	private final Image blackPiece = new Image("image/blackpiece.gif");
	private final Image whitePiece = new Image("image/whitepiece.gif");
	private GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);
	private Presenter presenter;
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	private Piece fromPiece;

	/**
	 * Pop up a warning dialog if a wrong move is taken by the player.
	 */
	private static class WarningDialog extends DialogBox {
		/**
		 * Initialize a new pop-up dialog, showing given warning message.
		 * 
		 * @param msg
		 *            the warning message to show in the dialog
		 */
		public WarningDialog(String msg) {
			setText("Warning: " + msg);
			Button ok = new Button("OK");
			ok.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					WarningDialog.this.hide();
				}
			});
			setWidget(ok);
			getElement().setPropertyString("text-align", "center");
		}
	}

	//
	// Styles in Graphics.ui.xml
	//
	interface Style extends CssResource {
		String button();

		String flashButton();

		String cellcontainer();

		String center();

		String cell();
	}

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics> {
	}

	@UiField
	Grid grid;
	@UiField
	Style style;
	@UiField
	Label status, phase, blackUnplacedMen, whiteUnplacedMen, blackLeftMen,
			whiteLeftMen;
	@UiField
	Button start, save, load, reset;

	public Graphics(Presenter presenter2) {
		presenter = presenter2;
		initWidget(uiBinder.createAndBindUi(this));
		grid.resize(7, 7);
		//
		// Initialize the view, initialize all elements and put them into the
		// grid.
		//
		int board[] = { 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1,
				1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1,
				0, 1, 0, 1, 0, 0, 1, 0, 0, 1 };
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				final int row = i;
				final int col = j;
				final SimplePanel cell = new SimplePanel();
				cell.setStyleName(style.cell());
				grid.setWidget(i, j, cell);
				if (board[i * 7 + j] == 1) {
					final Piece piece = new Piece(row, col);
					piece.setStyleName(style.button());
					//
					// Add handler for button click, which will call Presenter's
					// function and generate a new history item.
					//
					piece.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							presenter.clickOn(row, col, event);
						}
					});
					//
					// Add handler for button dragging, dragging should only be
					// used to move pieces.
					//
					piece.addDragStartHandler(new DragStartHandler() {
						@Override
						public void onDragStart(DragStartEvent event) {
							int status = piece.getStatus();
							if (status != 0) {
								event.preventDefault();
								fromPiece = piece;
								if (piece.getStatus() == 1) {
									event.getDataTransfer().setDragImage(
											getImageElement(Color.BLACK), 12,
											12);
								} else {
									event.getDataTransfer().setDragImage(
											getImageElement(Color.WHITE), 12,
											12);
								}
							}
						}
					});
					piece.addDragOverHandler(new DragOverHandler() {
						@Override
						public void onDragOver(DragOverEvent event) {
							if (fromPiece != piece)
								event.preventDefault();
						}
					});
					piece.addDropHandler(new DropHandler() {
						@Override
						public void onDrop(DropEvent event) {
							if (piece.getStatus() == 0) {
								event.preventDefault();
								presenter.moveMan(fromPiece.getX(),
										fromPiece.getY(), piece.getX(),
										piece.getY(), event);
							}
						}
					});
					piece.setEnabled(false);
					cell.add(piece);
					pieces.add(piece);
				}
			}
		}
		//
		// Add handler for top buttons.
		//
		start.setEnabled(true);
		save.setEnabled(false);
		load.setEnabled(false);
		reset.setEnabled(false);
		start.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.commandWrapper("start");
			}
		});
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.saveGame();
			}
		});
		load.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.loadGame();
			}
		});
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.commandWrapper("reset");
			}
		});
		setPieceStat("9999");
	}

	/**
	 * Send warning message through a pop-up dialog at given place.
	 * 
	 * @param msg
	 *            the warning message to show
	 * @param left
	 *            the x coordinate of the dialog
	 * @param top
	 *            the y coordinate of the dialog
	 * @return void
	 */
	public static void sendWarning(String msg, int left, int top) {
		WarningDialog warningDialog = new WarningDialog(msg);
		warningDialog.setPopupPosition(left, top);
		warningDialog.show();
	}
	
	@Override
	public void setPiece(Color color, int x, int y) {
		Piece piece = (Piece) ((SimplePanel) grid.getWidget(x, y)).getWidget();
		String fromColor = piece.getElement().getStyle().getBackgroundColor();
		String toColor;
		if (color == null) {
			toColor = "orangered";
			piece.setStatus(0);
			piece.getElement().setAttribute("draggable", "false");
		} else {
			toColor = color.name();
			if (color == Color.BLACK)
				piece.setStatus(1);
			else
				piece.setStatus(2);
			if (presenter.getPhase() != 1){
				piece.getElement().setAttribute("draggable", "true");
			}
		}
		PieceColorAnimation animation = new PieceColorAnimation(piece,
				fromColor, toColor);
		animation.run(250);
	}

	@Override
	public void setHighlighted(int x, int y, boolean highlighted) {
		Button button = (Button) ((SimplePanel) grid.getWidget(x, y))
				.getWidget();
		button.getElement().getStyle().setProperty("background", "Orange");
	}

	@Override
	public void setTurn(Color color) {
		if (color == null) {
			status.setText("---Not start yet---");
		} else {
			if (color != presenter.getPlayerColor())
				status.setText("---Your oponent's turn---");
			else
				status.setText("---Your turn---");
		}
	}

	@Override
	public void setPhase(int phase) {
		if (phase == 0)
			this.phase.setText("");
		else
			this.phase.setText("Phase " + phase);
	}

	public Piece getPiece(int i) {
		return pieces.get(i);
	}

	public void setPieceStat(String pieceStat) {
		String fromText, toText;
		fromText = blackUnplacedMen.getText();
		toText = "Unplaced: " + pieceStat.substring(0, 1);
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					blackUnplacedMen, fromText, toText);
			animation.run(200);
		}
		fromText = blackLeftMen.getText();
		toText = "Left: " + pieceStat.substring(1, 2);
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					blackLeftMen, fromText, toText);
			animation.run(200);
		}
		fromText = whiteUnplacedMen.getText();
		toText = "Unplaced: " + pieceStat.substring(2, 3);
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					whiteUnplacedMen, fromText, toText);
			animation.run(200);
		}
		fromText = whiteLeftMen.getText();
		toText = "Left: " + pieceStat.substring(3, 4);
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					whiteLeftMen, fromText, toText);
			animation.run(200);
		}
		if (pieceStat.substring(1, 2).equals("2"))
			setResult(Color.WHITE);
		if (pieceStat.substring(3, 4).equals("2"))
			setResult(Color.BLACK);
	}

	public void setRemovalTurn(Color removalTurn) {
		if (removalTurn != presenter.getPlayerColor())
			status.setText("---Your oponent's turn to remove---");
		else
			status.setText("---Your turn to remove---");
	}


	
	public void setTopButtonStatus(boolean start, boolean save, boolean load, boolean reset){
		this.start.setEnabled(start);
		this.save.setEnabled(save);
		this.load.setEnabled(load);
		this.reset.setEnabled(reset);
	}
	
	private void setResult(Color gameResult) {
		phase.setText("");
		if (gameResult != presenter.getPlayerColor())
			status.setText("You lost...");
		else
			status.setText("You won!");
		for (Piece piece : pieces) {
			piece.setEnabled(false);
		}
	}
	
	private Element getImageElement(Color color) {
		if (color == Color.BLACK) {
			return blackPiece.getElement();
		} else {
			return whitePiece.getElement();
		}
	}
}
