package com.google.gwt.faintynmm.client.ui;

import java.util.ArrayList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Graphics extends Composite implements Presenter.View {

	private static GraphicsUiBinder uiBinder = GWT
			.create(GraphicsUiBinder.class);
	private Presenter presenter;
	private ArrayList<Piece> pieces;
	private Piece fromPiece, toPiece;
	private final Image blackPiece = new Image("image/blackpiece.gif");
	private final Image whitePiece = new Image("image/whitepiece.gif");

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
	Button start, reset;

	public Graphics() {
		presenter = new Presenter(this);
		pieces = new ArrayList<Piece>();
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
					final Piece piece = new Piece(i * 7 + j);
					piece.setStyleName(style.button());
					//
					// Add handler for button click, which will call Presenter's
					// function and generate a new history item.
					//
					piece.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							presenter.clickOn(row, col, event);
							History.newItem(getStateString());
						}
					});
					//
					// Add handler for button dragging.
					//
					piece.addDragStartHandler(new DragStartHandler() {
						@Override
						public void onDragStart(DragStartEvent event) {
							int status = piece.getStatus();
							if (status != 0) {
								fromPiece = piece;
								String color = piece.getElement().getStyle()
										.getBackgroundColor();
								event.setData("text", color);
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
					piece.addDragEndHandler(new DragEndHandler() {
						@Override
						public void onDragEnd(DragEndEvent event) {
							if (toPiece != null) {
								piece.getElement().getStyle()
										.setBackgroundColor("OrangeRed");
								toPiece = null;
							}
						}
					});
					piece.addDropHandler(new DropHandler() {
						@Override
						public void onDrop(DropEvent event) {
							if (piece.getStatus() == 0) {
								event.preventDefault();
								String color = event.getData("text");
								piece.getElement().getStyle()
										.setProperty("background", color);
								toPiece = piece;
							}
						}
					});
					// piece.getElement().setDraggable(Element.DRAGGABLE_FALSE);
					piece.setEnabled(false);
					cell.add(piece);
					pieces.add(piece);
				}
			}
		}
		//
		// Add handler for history change, which will parse the current state
		// string and set up the view.
		//
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String newToken = event.getValue();
				start.setEnabled(false);
				reset.setEnabled(true);
				presenter.parseStateString(newToken);
			}
		});
		//
		// Add handler for start and reset button.
		//
		reset.setEnabled(false);
		start.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.reset();
				for (Piece piece : pieces) {
					piece.getElement().getStyle()
							.setProperty("background", "OrangeRed");
					piece.setEnabled(true);
					piece.setStatus(0);
				}
				reset.setEnabled(true);
				start.setEnabled(false);
				setTurn(presenter.getTurn());
				setPhase(presenter.getPhase());
				History.newItem(getStateString());
			}
		});
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.reset();
				for (Piece piece : pieces) {
					piece.getElement().getStyle()
							.setProperty("background", "OrangeRed");
					piece.setEnabled(false);
					piece.setStatus(0);
				}
				reset.setEnabled(false);
				start.setEnabled(true);
				setTurn(null);
				setPhase(0);
				History.newItem(getStateString());
			}
		});
		setPieceStat("9999");
	}

	/**
	 * Generate the string representing current game state.
	 * 
	 * @return the string of current game state
	 */
	private String getStateString() {
		StringBuilder stateString = new StringBuilder();
		stateString.append(presenter.getPhase());
		stateString.append(colorToInt(presenter.getTurn()));
		stateString.append(colorToInt(presenter.getRemovalTurn()));
		stateString.append(presenter.getPieceStat());
		for (Piece piece : pieces) {
			stateString.append(piece.getStatus());
		}
		return stateString.toString();
	}

	private int colorToInt(Color color) {
		if (color == null)
			return 0;
		else if (color == Color.BLACK)
			return 1;
		else
			return 2;
	}

	@Override
	public void setPiece(Color color, int x, int y) {
		Piece piece = (Piece) ((SimplePanel) grid.getWidget(x, y)).getWidget();
		if (color == null) {
			piece.getElement().getStyle()
					.setProperty("background", "OrangeRed");
			piece.setStatus(0);
		} else {
			piece.getElement().getStyle()
					.setProperty("background", color.name());
			if (color == Color.BLACK)
				piece.setStatus(1);
			else
				piece.setStatus(2);
		}
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
			status.setText("");
		} else {
			status.setText(color.name() + "'s turn");
		}
	}

	@Override
	public void setResult(Color gameResult) {
		phase.setText("");
		status.setText(gameResult.name() + " wins!");
		for (Piece piece : pieces) {
			piece.setEnabled(false);
		}
		start.setEnabled(true);
		reset.setEnabled(false);
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
	public void sendWarning(String msg, int left, int top) {
		WarningDialog warningDialog = new WarningDialog(msg);
		warningDialog.setPopupPosition(left, top);
		warningDialog.show();
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
		blackUnplacedMen.setText("Unplaced: " + pieceStat.substring(0, 1));
		blackLeftMen.setText("Left: " + pieceStat.substring(1, 2));
		whiteUnplacedMen.setText("Unplaced: " + pieceStat.substring(2, 3));
		whiteLeftMen.setText("Left: " + pieceStat.substring(3, 4));
		if (pieceStat.substring(1, 2).equals("2"))
			setResult(Color.BLACK);
		if (pieceStat.substring(3, 4).equals("2"))
			setResult(Color.WHITE);
	}

	private Element getImageElement(Color color) {
		if (color == Color.BLACK) {
			return blackPiece.getElement();
		} else if (color == Color.WHITE) {
			return whitePiece.getElement();
		} else {
			return null;
		}
	}
}
