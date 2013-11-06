package com.google.gwt.faintynmm.client.ui;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.faintynmm.client.FaintyNMMMessages;
import com.google.gwt.faintynmm.client.game.Color;
import com.google.gwt.faintynmm.client.game.Match;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Yiwei Li
 * 
 */
public class Graphics extends Composite implements Presenter.View {
	private final Image BLACK_PIECE = new Image("image/blackpiece.gif");
	private final Image WHITE_PIECE = new Image("image/whitepiece.gif");
	private FaintyNMMMessages messages = GWT.create(FaintyNMMMessages.class);
	private MatchListDialog matchListDialog;
	private NewMatchDialog newMatchDialog;
	private GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);
	private Presenter presenter;
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	private Piece fromPiece;

	//
	// Styles in Graphics.ui.xml
	//
	interface Style extends CssResource {
		String button();

		String topButton();

		String cellcontainer();

		String center();

		String cell();

		String glass();

		String unselectable();
	}

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics> {
	}

	@UiField
	Grid grid;
	@UiField
	Style style;
	@UiField
	Label matchInfo, status, phase, blackLabel, whiteLabel, blackUnplacedMen,
			whiteUnplacedMen, blackLeftMen, whiteLeftMen;
	@UiField
	Button startNewMatch, loadMatch;

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
			setModal(true);
			setGlassEnabled(true);
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

	/**
	 * Pop up window consisting of a match list of current player.
	 */
	private class MatchListDialog extends DialogBox {
		public MatchListDialog() {
			setModal(true);
			setAutoHideEnabled(true);
			setGlassEnabled(true);
			setGlassStyleName(style.glass());
			setPixelSize(377, 400);
			setAnimationEnabled(true);
		}

		public void updateAndShow(MatchCell matchCell, ArrayList<Match> matches) {
			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.getElement().getStyle().setOverflowY(Overflow.SCROLL);
			scrollPanel.setPixelSize(377, 400);
			if (matches.size() == 0) {
				Label label = new Label(messages.noMatchMsg());
				label.getElement().getStyle()
						.setProperty("fontFamily", "NightBits");
				label.getElement().getStyle().setProperty("fontSize", "large");
				label.addStyleName(style.center());
				scrollPanel.add(label);
			} else {
				CellList<Match> matchList = new CellList<Match>(matchCell);
				matchList.setRowData(matches);
				scrollPanel.add(matchList);
			}
			setWidget(scrollPanel);
			super.center();
			int left = getPopupLeft();
			int top = getPopupTop();
			this.setPopupPosition(left, top / 2);
			show();
		}
	}

	/**
	 * Pop up window allowing player to find new match.
	 */
	private class NewMatchDialog extends DialogBox {
		private final TextBox emailBox = new TextBox();
		private final Button sendButton = new Button(messages.sendButtonMsg());

		public NewMatchDialog() {
			setModal(true);
			setAutoHideEnabled(true);
			setGlassEnabled(true);
			setGlassStyleName(style.glass());
			setAnimationEnabled(true);

			VerticalPanel panel = new VerticalPanel();
			HorizontalPanel panel1 = new HorizontalPanel();
			HorizontalPanel panel2 = new HorizontalPanel();

			emailBox.getElement().getStyle()
					.setProperty("fontFamily", "NightBits");
			emailBox.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
						sendButton.click();
					}
				}
			});

			Label label1 = new Label(messages.inviteFriendMsg());
			label1.getElement().getStyle().setProperty("fontSize", "large");
			label1.getElement().getStyle()
					.setProperty("fontFamily", "NightBits");
			label1.addStyleName(style.unselectable());

			sendButton.setStyleName(style.topButton());
			sendButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.startNewMatchGivenEmail(emailBox.getText());
					NewMatchDialog.this.hide();
				}
			});
			panel1.add(label1);
			panel1.add(emailBox);
			panel1.add(sendButton);
			panel1.setStyleName(style.center());
			panel1.setCellVerticalAlignment(label1,
					HasVerticalAlignment.ALIGN_MIDDLE);
			panel1.setCellVerticalAlignment(emailBox,
					HasVerticalAlignment.ALIGN_MIDDLE);
			panel1.setCellVerticalAlignment(sendButton,
					HasVerticalAlignment.ALIGN_MIDDLE);

			Label label2 = new Label(messages.useAutoMatchMsg());
			label2.getElement().getStyle().setProperty("fontSize", "large");
			label2.getElement().getStyle()
					.setProperty("fontFamily", "NightBits");
			label2.addStyleName(style.unselectable());
			Button autoMatchButton = new Button(messages.automatchButtonMsg());
			autoMatchButton.setStyleName(style.topButton());
			autoMatchButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.startNewMatchWithAutoMatch();
					NewMatchDialog.this.hide();
				}
			});
			panel2.add(label2);
			panel2.add(autoMatchButton);
			panel2.setStyleName(style.center());
			panel2.setCellVerticalAlignment(label2,
					HasVerticalAlignment.ALIGN_MIDDLE);
			panel2.setCellVerticalAlignment(autoMatchButton,
					HasVerticalAlignment.ALIGN_MIDDLE);

			panel.add(panel1);
			panel.add(panel2);
			setWidget(panel);
		}

		public void center() {
			super.center();
			int left = getPopupLeft();
			int top = getPopupTop();
			this.setPopupPosition(left, top / 2);
			emailBox.setText("");
			emailBox.setFocus(true);
		}
	}

	public Graphics(Presenter presenter2) {
		presenter = presenter2;
		// Initialize widget before using anything related to UiBinder.
		initWidget(uiBinder.createAndBindUi(this));

		matchListDialog = new MatchListDialog();
		newMatchDialog = new NewMatchDialog();
		grid.resize(7, 7);

		//
		// Set up internationalized strings.
		matchInfo.setText(messages.matchInfoNullMsg());
		startNewMatch.setText(messages.newMatchButtonMsg());
		loadMatch.setText(messages.loadMatchButtonMsg());
		blackLabel.setText(messages.black());
		whiteLabel.setText(messages.white());

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
					piece.getElement().setAttribute("draggable", "true");
					cell.add(piece);
					pieces.add(piece);
				}
			}
		}
		//
		// Add handler for top buttons.
		//
		startNewMatch.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				newMatchDialog.center();
			}
		});
		loadMatch.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.loadMatchList();
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
	public static void showWarning(String msg, int left, int top) {
		WarningDialog warningDialog = new WarningDialog(msg);
		warningDialog.setAnimationEnabled(true);
		warningDialog.setAutoHideEnabled(true);
		warningDialog.setPopupPosition(left, top);
		warningDialog.show();
	}

	public void showMatchListDialog(MatchCell matchCell,
			ArrayList<Match> matches) {
		matchListDialog.updateAndShow(matchCell, matches);
	}

	public void hideMatchListDialog() {
		matchListDialog.hide();
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
			if (presenter.getPhase() != 1) {
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
			status.setText("");
		} else {
			if (color != presenter.getPlayerColor())
				status.setText(messages.statusOppoTurnMsg());
			else
				status.setText(messages.statusOwnTurnMsg());
		}
	}

	@Override
	public void setPhase(int phase) {
		if (phase == 0)
			this.phase.setText("");
		else
			this.phase.setText(messages.phaseMsg(Integer.toString(phase)));
	}

	public Piece getPiece(int i) {
		return pieces.get(i);
	}

	public void setPieceStat(String pieceStat) {
		String fromText, toText;
		fromText = blackUnplacedMen.getText();
		toText = messages.unplacedMenMsg(pieceStat.substring(0, 1));
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					blackUnplacedMen, fromText, toText);
			animation.run(200);
		}
		fromText = blackLeftMen.getText();
		toText = messages.leftMenMsg(pieceStat.substring(1, 2));
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					blackLeftMen, fromText, toText);
			animation.run(200);
		}
		fromText = whiteUnplacedMen.getText();
		toText = messages.unplacedMenMsg(pieceStat.substring(2, 3));
		if (!fromText.equals(toText)) {
			InfoUpdateAnimation animation = new InfoUpdateAnimation(
					whiteUnplacedMen, fromText, toText);
			animation.run(200);
		}
		fromText = whiteLeftMen.getText();
		toText = messages.leftMenMsg(pieceStat.substring(3, 4));
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
			status.setText(messages.statusOppoRemoveTurnMsg());
		else
			status.setText(messages.statusOwnRemoveTurnMsg());
	}

	private void setResult(Color gameResult) {
		phase.setText("");
		if (gameResult != presenter.getPlayerColor())
			status.setText(messages.statusLoserMsg());
		else
			status.setText(messages.statusWinnerMsg());
		for (Piece piece : pieces) {
			piece.setEnabled(false);
		}
	}

	private Element getImageElement(Color color) {
		if (color == Color.BLACK) {
			return BLACK_PIECE.getElement();
		} else {
			return WHITE_PIECE.getElement();
		}
	}

	public void updateMatchInfo(String opponentId, String matchId) {
		matchInfo.setText(messages.matchInfoMsg(opponentId, matchId));
	}

	public void resetMatchInfo() {
		matchInfo.setText(messages.matchInfoNullMsg());
	}
	
	public FaintyNMMMessages getMessages(){
		return messages;
	}
}
