package com.google.gwt.faintynmm.client.ui;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Graphics extends Composite implements Presenter.View {

	private static GraphicsUiBinder uiBinder = GWT
			.create(GraphicsUiBinder.class);

	private Presenter presenter;
	private ArrayList<Piece> pieces;

	private static class WarningDialog extends DialogBox {
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
		int board[] = { 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1,
				1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1,
				0, 1, 0, 1, 0, 0, 1, 0, 0, 1 };
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				final int row = i;
				final int col = j;
				SimplePanel cell = new SimplePanel();
				cell.setStyleName(style.cell());
				grid.setWidget(i, j, cell);
				if (board[i * 7 + j] == 1) {
					Piece piece = new Piece(i * 7 + j);
					piece.setStyleName(style.button());
					piece.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							presenter.clickOn(row, col, event);
							History.newItem(getStateString());
						}
					});
					piece.setEnabled(false);
					cell.add(piece);
					pieces.add(piece);
				}
			}
		}
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String newToken = event.getValue();
				presenter.parseStateString(newToken);
			}
		});

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
	}

	private String getStateString() {
		StringBuilder stateString = new StringBuilder();
		stateString.append(presenter.getPhase());
		stateString.append(colorToInt(presenter.getTurn()));
		stateString.append(colorToInt(presenter.getRemovalTurn()));
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
		Piece piece = (Piece) ((SimplePanel) grid.getWidget(x, y))
				.getWidget();
		if (color == null) {
			piece.getElement().getStyle()
					.setProperty("background", "OrangeRed");
			piece.setStatus(0);
		} else {
			piece.getElement().getStyle()
					.setProperty("background", color.name());
			if (color == Color.BLACK) piece.setStatus(1);
			else piece.setStatus(2);
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

	public ArrayList<Piece> getPieces() {
		return pieces;
	}
}
