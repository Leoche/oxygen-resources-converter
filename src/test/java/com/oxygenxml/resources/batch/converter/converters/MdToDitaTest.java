package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.ProgressDialogInteractorTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;

/**
 * JUnit test for the Markdown to DITA conversion.
 * 
 * @author Cosmin Duna
 */
public class MdToDitaTest {

	@Test
	public void test() throws TransformerException, IOException {
		
		File sample  = new File("test-sample/markdownTest.md");		
		File goodSample = new File("test-sample/goodMdToDita.dita");
		File outputFolder = sample.getParentFile();
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File fileToRead = ConverterFileUtils.getOutputFile(sample, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.MD_TO_DITA, inputFiles, outputFolder, false);

			assertTrue(FileComparationUtil.compareLineToLine(goodSample, fileToRead));

		} finally {
			try {
				Files.delete(Paths.get(fileToRead.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p><b>Description:</b> We have an invalid Markdown file. Check if a problem is reported.</p>
	 * <p><b>Bug ID:</b> EXM-41340</p>
	 * 
	 * @author cosmin_duna
	 */
	@Test
	public void testDocumentWithProblems_EXM_41340() throws TransformerException, IOException {
		
		File sample  = new File("test-sample/invalidDoc.md");		
		File outputFolder = sample.getParentFile();
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.MD_TO_DITA, inputFiles, outputFolder, false);
			assertFalse("The file shouldn't be generated.", convertedFile.exists());

			// Check the reported problem
			List<Exception> reportedProblems = problemReporter.getReportedProblems();
			assertEquals(1, reportedProblems.size());
			assertEquals("Failed to parse Markdown: Header level raised from 2 to 4 without intermediate header level", reportedProblems.get(0).getMessage());
			
		} finally {
			if(convertedFile.exists()) {
				try {
					Files.delete(Paths.get(convertedFile.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
		
}