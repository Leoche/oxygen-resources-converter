package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.worker.ConvertorWorker;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

public class ConvertorDialog extends OKCancelDialog implements BatchConverterInteractor{

	private InputPanel inputPanel;
	
	private	OutputPanel outputPanel;

	ConvertorWorker convertorWorker;
	
	private JFrame parentFrame;

	private Translator translator;

	/**
	 * The type of convertor.
	 */
	private String convertorType;

	
	public ConvertorDialog(String convertorType, List<String> toConvertFiles, JFrame parentFrame, Translator translator) {
		super(parentFrame, "" , true);
		this.convertorType = convertorType;
		this.parentFrame = parentFrame;
		this.translator = translator;

		inputPanel = new InputPanel(convertorType, translator, this);
		outputPanel = new OutputPanel(translator);
		
		initGUI(translator);
		
		if(!toConvertFiles.isEmpty()){
			getOkButton().setEnabled(true);
			inputPanel.addFilesInTable(toConvertFiles);
		}
		else{
			getOkButton().setEnabled(false);
		}
		
		setTitle(translator.getTranslation(Tags.DIALOG_TITLE, convertorType));
		setOkButtonText(translator.getTranslation(Tags.CONVERT_BUTTON, ""));
		setResizable(true);
		setMinimumSize(new Dimension(350, 300));
		setSize(new Dimension(420, 350));
		setLocationRelativeTo(parentFrame);
		setVisible(true);
	}


	private void initGUI(Translator translator){
		
		JPanel convertorPanel = new JPanel( new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		//-----Add the input panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 20, 0);
		convertorPanel.add(inputPanel, gbc);
	
		//-----Add the output panel
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		convertorPanel.add(outputPanel, gbc);
		
		this.add(convertorPanel);
	}
	
	/**
	 * Convert pressed.
	 */
	@Override
	protected void doOK() {

		if (outputPanel.getOutputPath().isEmpty()) {
			PluginWorkspaceProvider.getPluginWorkspace().showWarningMessage(translator.getTranslation(Tags.EMPTY_OUTPUT_MESSAGE,""));
		} else {

			final ProgressDialog progressDialog = new ProgressDialog(parentFrame, translator);

			convertorWorker = new ConvertorWorker(convertorType, this, progressDialog);

			progressDialog.addCancelActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					convertorWorker.cancel(true);
					progressDialog.dispose();
				}
			});

			convertorWorker.execute();

			super.doOK();
		}
	}




	@Override
	public List<File> getInputFiles() {
		return ConverterFileUtils.convertToFile(inputPanel.getFilesFromTable());
	}


	@Override
	public File getOutputFolder() {
		return new File(outputPanel.getOutputPath());
	}


	@Override
	public void setOutputFolder(String text) {
		outputPanel.setOutputPath(text);
	}


	@Override
	public void setEnableConvert(boolean state) {
		getOkButton().setEnabled(state);
	}

	
}