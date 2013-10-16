package com.google.gwt.faintynmm.client.ui;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Label;

public class InfoUpdateAnimation extends Animation {
	private Label infoLabel;
	private String toText;
	
	public InfoUpdateAnimation(Label infoLabel, String fromText, String toText) {
		this.infoLabel = infoLabel;
		this.toText = toText;
		infoLabel.setText(fromText + "\n" + toText);
	}

	@Override
	protected void onUpdate(double progress) {
		infoLabel.getElement().setScrollTop((int)(24*progress));
		if (progress == 1.0){
			infoLabel.setText(toText);
		}
	}
}
