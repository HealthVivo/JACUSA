package accusa2.cli.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import accusa2.cli.parameters.SampleParameters;

public class MinBASQOption extends AbstractACOption {

	private SampleParameters sampleA;
	private SampleParameters sampleB;
	
	public MinBASQOption(SampleParameters sampleA, SampleParameters sampleB) {
		this.sampleA = sampleA;
		this.sampleB = sampleB;

		opt = "q";
		longOpt = "min-basq";
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("filter positions with base quality < " + longOpt.toUpperCase() + " \n default: " + sampleA.getMinBASQ())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	byte minBASQ = Byte.parseByte(value);
	    	if(minBASQ < 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " = " + minBASQ + " not valid.");
	    	}
	    	sampleA.setMinBASQ(minBASQ);
	    	sampleB.setMinBASQ(minBASQ);
	    }
	}

}