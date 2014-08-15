package accusa2.method.statistic;

import umontreal.iro.lecuyer.probdist.ChiSquareDist;
import umontreal.iro.lecuyer.probdistmulti.DirichletDist;
import accusa2.cli.Parameters;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.process.pileup2Matrix.AbstractPileup2Prob;
import accusa2.process.pileup2Matrix.BASQ;
import accusa2.util.MathUtil;

/**
 * 
 * @author michael
 */

public final class MethodOfMomentsStatistic implements StatisticCalculator {

	protected final Parameters parameters; 
	
	protected final AbstractPileup2Prob pileup2Prob;

	protected ChiSquareDist dist = new ChiSquareDist(6);
	
	public MethodOfMomentsStatistic(Parameters parameters) {
		this.parameters 	= parameters;
		
		pileup2Prob 		= new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new MethodOfMomentsStatistic(parameters);
	}

	protected double[][] getPileup2Probs(final int[] bases, final Pileup[] pileups) {
		final double[][] probs = new double[pileups.length][bases.length];

		for (int pileupI = 0; pileupI < pileups.length; pileupI++) {
			// sum the probabilities giving alpha 
			final double[] prob = pileup2Prob.calculate(bases, pileups[pileupI]);
			for (int i = 0; i < bases.length; ++i) {
				probs[pileupI][i] = prob[i];
			}
		}
		return probs;
	}

	protected int estimateMeanCoverage(Pileup[] pileups) {
		if(pileups.length == 1) { 
			return pileups[0].getCoverage();
		}

		int coverage = 0;
		for(Pileup pileup : pileups) {
			coverage += pileup.getCoverage();
		}
		return (int)Math.round((double)coverage / (double)pileups.length);
	}

	protected double[] getWeights(final int[] bases, final Pileup[] pileups) {
		final double[] weights = new double[bases.length];
		final int n = pileups.length;

		for (int i = 0; i < n; ++i) {
			final Pileup pileup = pileups[i];
			weights[i] = (double)pileup.getCoverage() / (double)n;
		}
		
		return weights;
	}
	
	protected double[] getMean(final int[] bases, final Pileup[] pileups) {
		final double[][] probs = getPileup2Probs(bases, pileups);
		final double[] mean = MathUtil.mean(probs);
		
		return mean;
	}
	
	protected double[] estimateAlpha(final int bases[], final Pileup[] pileups) {
		double[] mean = getMean(bases, pileups); 
		double[] weights = getWeights(bases, pileups);
		double[] variance = getVariance(bases, pileups);
		
		// TODO do the actual estimation
		final double[] alpha = mean;
		
		
		return alpha;
	}

	protected double getDensity(final int[] bases, final DirichletDist dirichlet, final Pileup[] pileups) {
		double density = 0.0;

		for (final Pileup pileup : pileups) {
			double[] prob = pileup2Prob.calculate(bases, pileup);
			density += Math.log(Math.max(Double.MIN_VALUE, dirichlet.density(prob)));
		}

		return density;
	}

	protected double[] getVariance(final int[] base, final Pileup[] pileups) {
		double[] var = new double [base.length];
		
		return var;
	}
	
	public double getStatistic(final ParallelPileup parallelPileup) {
		final int bases[] = {0, 1, 2, 3};
		//final int bases[] = parallelPileup.getPooledPileup().getAlleles();

		// first sample(s)
		double[] alpha1 = estimateAlpha(bases, parallelPileup.getPileups1());
		DirichletDist dirichlet1 = new DirichletDist(alpha1);
		double density1 = getDensity(bases, dirichlet1, parallelPileup.getPileups1());
		
		// second sample(s)
		double[] alpha2 = estimateAlpha(bases, parallelPileup.getPileups2());
		DirichletDist dirichlet2 = new DirichletDist(alpha2);
		double density2 = getDensity(bases, dirichlet2, parallelPileup.getPileups2());
		
		// pooled sample(s)
		final Pileup[] pileupsP = new Pileup[parallelPileup.getN1() + parallelPileup.getN2()];
		System.arraycopy(parallelPileup.getPileups1(), 0, pileupsP, 0, parallelPileup.getPileups1().length);
		System.arraycopy(parallelPileup.getPileups2(), 0, pileupsP, parallelPileup.getPileups1().length, parallelPileup.getPileups2().length);
		double[] alphaP = estimateAlpha(bases, pileupsP);
		DirichletDist dirichletP = new DirichletDist(alphaP);
		double densityP = getDensity(bases, dirichletP, pileupsP);

		final double z = -2 * (densityP) + 2 * (density1 + density2);

		// only positive values are allowed
		if(z < 0.0 ) {
			return 1.0;
		}
		return 1 - dist.cdf(z);
	}

	@Override
	public boolean filter(double value) {
		return parameters.getT() < value;
	}

	@Override
	public String getDescription() {
		return "Method of Moments estimation";
	}

	@Override
	public String getName() {
		return "mom";
	}

}