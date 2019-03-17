package jacusa.io.format;

import jacusa.filter.FilterConfig;
import jacusa.io.format.PileupFormat;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.Result;
import jacusa.util.Util;

public class PileupResultFormat extends AbstractOutputFormat {

	private PileupFormat pileupFormat; 
	private boolean showReferenceBase;
	
	public PileupResultFormat(
			final BaseConfig baseConfig, 
			final FilterConfig fitlerConfig,
			final boolean showReferenceBase) {
		super('A', "pileup like ACCUSA result format");
		
		pileupFormat = new PileupFormat(baseConfig, showReferenceBase);
	}
	
	public String getHeader(String[] pathnames1, String[] pathnames2) {
		final StringBuilder sb = new StringBuilder();
		sb.append(getCOMMENT());

		sb.append("contig");
		sb.append(getSEP());

		sb.append("position");

		// (1) first sample infos
		for (int i = 0; i < pathnames1.length; ++i) {
			sb.append(getSEP());
			sb.append("strand1");
			sb.append(i + 1);
			sb.append(getSEP());
			sb.append("bases1");
			sb.append(i + 1);
			sb.append(getSEP());
			sb.append("quals1");
			sb.append(i + 1);
		}		
		// (2) second sample infos
		for (int i = 0; i < pathnames2.length; ++i) {
			sb.append(getSEP());
			sb.append("strand2");
			sb.append(i + 1);
			sb.append(getSEP());
			sb.append("bases2");
			sb.append(i + 1);
			sb.append(getSEP());
			sb.append("quals2");
			sb.append(i + 1);
		}

		sb.append(getSEP());
		sb.append("stat");
		
		sb.append(getSEP());
		sb.append("filter_info");

		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append("refBase");
		}

		return sb.toString();
	}
	
	@Override
	public String convert2String(final Result result) {
		final StringBuilder sb = new StringBuilder(pileupFormat.convert2String(result));
		// add unfiltered value
		sb.append(pileupFormat.getSEP());
		sb.append(Util.format(result.getStatistic()));
		
		sb.append(pileupFormat.getSEP());
		sb.append(result.getObject("filterInfo"));
		
		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append(result.getParellelPileup().getPooledPileup().getRefBase());
		}

		return sb.toString();
	}

	public char getCOMMENT() {
		return pileupFormat.getCOMMENT();
	}
	
	public char getEMPTY() {
		return pileupFormat.getEMPTY();
	}
	
	public char getSEP() {
		return pileupFormat.getSEP(); 
	}
	
	public char getSEP2() {
		return pileupFormat.getSEP2(); 
	}
	
}