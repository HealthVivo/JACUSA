package jacusa.cli.options;

import jacusa.cli.parameters.CLI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class VersionOption extends AbstractACOption {

	private CLI cmd;

	public VersionOption(CLI cmd) {
		opt = "v";
		longOpt = "version";

		this.cmd = cmd;
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
	    	cmd.printUsage(); 
	    	System.exit(0);
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.hasArg(false)
	        .withDescription("Print version information.")
	        .create(opt);
	}

}