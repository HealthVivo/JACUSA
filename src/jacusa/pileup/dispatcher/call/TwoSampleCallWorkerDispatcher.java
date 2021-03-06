package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.TwoSampleCallParameters;
import jacusa.pileup.worker.TwoSampleCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class TwoSampleCallWorkerDispatcher extends AbstractCallWorkerDispatcher<TwoSampleCallWorker> {

	private TwoSampleCallParameters parameters;
	
	public TwoSampleCallWorkerDispatcher(
			String[] pathnames1, 
			String[] pathnames2,
			final CoordinateProvider coordinateProvider,
			final TwoSampleCallParameters parameters) throws IOException {
		super(
				pathnames1,
				pathnames2,
				coordinateProvider, 
				parameters.getMaxThreads(),
				parameters.getOutput(),
				parameters.getFormat(),
				parameters.isSeparate()
		);
		
		this.parameters = parameters;
	}

	@Override
	protected TwoSampleCallWorker buildNextWorker() {
		return new TwoSampleCallWorker(
				this, 
				this.getWorkerContainer().size(),
				parameters);
	}

}