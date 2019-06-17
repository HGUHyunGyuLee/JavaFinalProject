package edu.handong.csee.java;

import edu.handong.csee.java.utils.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class JavaFinalProject implements Runnable {

	private ArrayList<Thread> threadsForZipFiles = new ArrayList<Thread>();
	private ArrayList<JavaFinalProject> projectRunners = new ArrayList<JavaFinalProject>();
	boolean help;
	private String input;
	private String output;
	private String zipFileName;
	private ArrayList<MyCSVParser> myParser = new ArrayList<MyCSVParser>();
	private ArrayList<Unzip> unzipper = new ArrayList<Unzip>();
	private Library<MyCSVParser> myParserLibrary = new Library<MyCSVParser>();
	private Library<Unzip> unzipperLibrary = new Library<Unzip>(unzipper);

	private Unzip singleUnzip = new Unzip();

	public JavaFinalProject(String input, String zipFileName) {
		this.zipFileName = zipFileName;
		this.input = input;
	}

	public JavaFinalProject() {
//empty constructor
	}

	public Unzip getUnzipper() {
		return singleUnzip;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void run() {
		singleUnzip = new Unzip(input + "/" + zipFileName);
		if (!zipFileName.endsWith(".zip"))
			try {
				throw new NotZipFileException();
			} catch (NotZipFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		singleUnzip.unzip();
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public void runForMain(String[] args) throws IOException, InterruptedException {
		Options options = createOptions();

		if (parseOptions(options, args)) {

			String inputFile = input;
			String outputFile = output;
			String secondOutputFile = outputFile.split(".csv")[0] + "2" + ".csv";
			try {
				File file = new File(inputFile);
				if (file == null)
					throw new NullPointerException();
				for (String files : file.list()) {
					projectRunners.add(new JavaFinalProject(input, files));
				}
				for (JavaFinalProject runner : projectRunners) {
					Thread thread = new Thread(runner);
					thread.start();
					threadsForZipFiles.add(thread);
				}
				for (Thread t : threadsForZipFiles)
					t.join();

				if (help) {
					printHelp(options);
					return;
				}
				System.out.println("You've put " + input + " as an input file");

				for (JavaFinalProject runner : projectRunners) {
					unzipper.add(runner.getUnzipper());
				}

				int j = 0;

				// unzipper = (ArrayList<Unzip>) unzipperLibrary.issueList();

				unzipperLibrary = new Library<Unzip>(unzipper);

				for (Unzip files : unzipper) {
					for (String names : files.getUnzippedFiles()) {
						myParser.add(new MyCSVParser(names));
						myParser.get(j++).ReadCSV();
					}
				}
				// myParser = (ArrayList<MyCSVParser>) myParserLibrary.issueList();

				myParserLibrary = new Library<MyCSVParser>(myParser);
				Collections.sort(myParser);
				writeOutput(outputFile, secondOutputFile);
			} catch (NullPointerException e) {
				System.out.println("Please check your input file again.");
				System.exit(1);
			}
	
		
			System.out.println("Your program is terminated.");
			System.out.println(myParserLibrary.length() + " exel files in " + unzipperLibrary.length() + " zipfiles are merged!" );
		}

	}

	public void writeOutput(String outputFile, String secondOutputFile) throws IOException {
		boolean firstOutput = true;
		boolean secondOutput = true;
		try {
			for (int i = 0; i < myParser.size(); i++) { // start from 0 to 9
				if (firstOutput) {
					myParser.get(0).write(myParser.get(i), outputFile, firstOutput);
					firstOutput = false;
				} else if (secondOutput) {
					myParser.get(1).write(myParser.get(i), secondOutputFile, secondOutput);
					secondOutput = false;
				} else if (i % 2 == 0) { // 2,4,6,8
					myParser.get(0).write(myParser.get(i), outputFile, secondOutput);
				} else { // 짝 3,5,7,9
					myParser.get(1).write(myParser.get(i), secondOutputFile, firstOutput);
				}

			}
		} catch (InvalidFormatException e) {
			e.getMessage();
		}

	}

	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);

			input = cmd.getOptionValue("i");
			output = cmd.getOptionValue("o");
			help = cmd.hasOption("h");

		} catch (Exception e) {
			printHelp(options);
			return false;
		}

		return true;
	}

	// Definition Stage
	private Options createOptions() {
		Options options = new Options();

		options.addOption(Option.builder("h").longOpt("help").desc("Help").build());
		options.addOption(Option.builder("i").longOpt("input").desc("Set an input file path").hasArg()
				.argName("Input path").required().build());
		options.addOption(Option.builder("o").longOpt("output").desc("Set an output file path").hasArg()
				.argName("Output path").required().build());

		return options;
	}

	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "Java Final Project";
		String footer = "";
		formatter.printHelp("JavaFinalProject", header, options, footer, true);
	}
}
