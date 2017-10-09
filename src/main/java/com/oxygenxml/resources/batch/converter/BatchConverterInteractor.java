package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.util.List;

/**
 * Interactor with batch converter.
 * @author intern4
 *
 */
public interface BatchConverterInteractor {

	/**
	 * Get the input files URLs in String format.
	 * @return List with input files URLs in String format.
	 */
	public List<File> getInputFiles();
	
	/**
	 * Get the output folder path.
	 * @return The path of output folder.
	 */
	public File getOutputFolder();
	
	/**
	 * Set the output folder path.
	 * @param text The path of output folder.
	 */
	public void setOutputFolder(String text);
	
	
	/**
	 * Set enable/ disable the convert button.
	 * @param state <code>true</code> to set enable, <code>false</code> to set disable.
	 */
	public void setEnableConvert(boolean state);
}