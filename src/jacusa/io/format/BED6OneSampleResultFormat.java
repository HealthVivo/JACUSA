package jacusa.io.format;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Result;
import jacusa.util.Util;

public class BED6OneSampleResultFormat extends BED6ResultFormat {
	
	public BED6OneSampleResultFormat(final AbstractParameters parameters) {
		super('B', "Default", parameters);
	}

	@Override
	public String convert2String(Result result) {
		final ParallelPileup parallelPileup = result.getParellelPileup();
		final double statistic = result.getStatistic();
		final StringBuilder sb = new StringBuilder();

		// coordinates
		sb.append(parallelPileup.getContig());
		sb.append(SEP);
		sb.append(parallelPileup.getStart() - 1);
		sb.append(SEP);
		sb.append(parallelPileup.getEnd());
		
		sb.append(SEP);
		sb.append("variant");
		
		sb.append(SEP);
		if (Double.isNaN(statistic)) {
			sb.append("NA");
		} else {
			sb.append(Util.format(statistic));
		}

		sb.append(SEP);
		sb.append(parallelPileup.getStrand().character());

		// (1) first pileups / actual sample
		addPileups(sb, parallelPileup.getPileups2());
		
		// (2) first pileups / actual sample
		addPileups(sb, parallelPileup.getPileups1());
		
		sb.append(getSEP());
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (parameters.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		if (parameters.showReferenceBase()) {
			sb.append(getSEP());
			sb.append(parallelPileup.getPooledPileup().getRefBase());
		}
		
		return sb.toString();		
	}

	@Override
	public String getHeader(String[] pathnames1, String[] pathnames2) {
		final StringBuilder sb = new StringBuilder();

		sb.append(COMMENT);

		// position (0-based)
		sb.append("contig");
		sb.append(getSEP());
		sb.append("start");
		sb.append(getSEP());
		sb.append("end");
		sb.append(getSEP());

		sb.append("name");
		sb.append(getSEP());

		// stat	
		sb.append("stat");
		sb.append(getSEP());
		
		sb.append("strand");
		sb.append(getSEP());
		
		// (1) first sample  infos
		addSampleHeader(sb, '1', pathnames1.length);
		sb.append(getSEP());
		// (2) second sample  infos
		addSampleHeader(sb, '2', pathnames1.length);

		sb.append(getSEP());
		sb.append("info");
		
		// add filtering info
		if (parameters.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append("filter_info");
		}

		if (parameters.showReferenceBase()) {
			sb.append(getSEP());
			sb.append("refBase");
		}
		
		return sb.toString();
	}
	
}
