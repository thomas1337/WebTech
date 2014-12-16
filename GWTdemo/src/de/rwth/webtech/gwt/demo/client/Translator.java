package de.rwth.webtech.gwt.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import de.rwth.webtech.gwt.demo.client.TranslatorLogic;

/* 
 * This file will be converted to client Javascript code.
 */
public class Translator implements EntryPoint {

	TextBox inputBox;
	Button translateBtn;
	Label output;
	
	TranslatorLogic logic;
	
	@Override
	public void onModuleLoad() {
		// Create Logic.
		logic = new TranslatorLogic();
		
		// Define UI elements.
		inputBox = new TextBox();
		translateBtn = new Button(); 
		output = new Label();
		
		// Config UI elements.
		translateBtn.setText("1337 it");
		output.setText("Here will be your 1337ed input");
		translateBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				output.setText(logic.translate(inputBox.getText()));
			}
		});
		
		// Add elements to html.
		RootPanel.get("inputArea").add(inputBox);
		RootPanel.get("inputArea").add(translateBtn);
		RootPanel.get("outputArea").add(output);
		
	}

}
