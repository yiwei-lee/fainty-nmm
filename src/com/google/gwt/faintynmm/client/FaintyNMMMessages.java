package com.google.gwt.faintynmm.client;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface FaintyNMMMessages extends Messages {
	@DefaultMessage("Fainty''s Nine Men''s Morris")
	String faintyNMM();

	@DefaultMessage("Your web browser must have JavaScript enabled in order for this application to display correctly.")
	String jsNotEnabled();

	@DefaultMessage("Welcome, {0}!")
	String welcomeLabelMsg(String playerName);

	@DefaultMessage("Please sign in with your Facebook Account.")
	String loginMsg();
	
	@DefaultMessage("Start New Match")
	String newMatchButtonMsg();

	@DefaultMessage("Load Match")
	String loadMatchButtonMsg();

	@DefaultMessage("Playing with : {0}\nMatch ID : {1}")
	String matchInfoMsg(String opponentName, String matchId);

	@DefaultMessage("Not in match")
	String matchInfoNullMsg();

	@DefaultMessage("Phase {0}")
	String phaseMsg(String phase);

	@DefaultMessage("---Your turn---")
	String statusOwnTurnMsg();

	@DefaultMessage("---Your opponent''s turn---")
	String statusOppoTurnMsg();

	@DefaultMessage("---Your turn to remove---")
	String statusOwnRemoveTurnMsg();

	@DefaultMessage("---Your opponent''s turn to remove---")
	String statusOppoRemoveTurnMsg();

	@DefaultMessage("You win!")
	String statusWinnerMsg();

	@DefaultMessage("You lose...")
	String statusLoserMsg();

	@DefaultMessage("Unplaced : {0}")
	String unplacedMenMsg(String number);

	@DefaultMessage("Left : {0}")
	String leftMenMsg(String number);

	@DefaultMessage("Black")
	String black();

	@DefaultMessage("White")
	String white();

	@DefaultMessage("Invite a friend via E-mail : ")
	String inviteFriendMsg();

	@DefaultMessage("Send")
	String sendButtonMsg();

	@DefaultMessage("Or use : ")
	String useAutoMatchMsg();

	@DefaultMessage("Auto Match")
	String automatchButtonMsg();

	@DefaultMessage("You haven''t played any match yet!")
	String noMatchMsg();
	
	@DefaultMessage("Delete")
	String deleteButtonMsg();
	
	@DefaultMessage("Opponent : {0}")
	String opponentMsg(String opponentName);
	
	@DefaultMessage("Current : {0}")
	String currentPlayerMsg(String playerName);
	
	@DefaultMessage("Last Update : {0}")
	String lastUpdateDateMsg(String lastUpdateDate);
	
	@DefaultMessage("Do you really want to delete the match?")
	String confirmDeleteMatchMsg();
	
	@DefaultMessage("Surrender")
	String surrenderButtonMsg();

	@DefaultMessage("Do you really want to surrender this match?")
	String surrenderMsg();

	@DefaultMessage("Play With AI")
	SafeHtml playWithAiMsg();

	@DefaultMessage("Rating : {0}")
	String ratingMsg(String rating);
}
