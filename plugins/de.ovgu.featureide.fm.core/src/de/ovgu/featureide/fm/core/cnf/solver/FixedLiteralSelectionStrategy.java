package de.ovgu.featureide.fm.core.cnf.solver;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;
import static org.sat4j.core.LiteralsUtils.var;

import org.sat4j.minisat.core.IPhaseSelectionStrategy;

public class FixedLiteralSelectionStrategy implements IPhaseSelectionStrategy {

	private static final long serialVersionUID = -1687370944480053808L;

	private final int[] model, phase;

	public FixedLiteralSelectionStrategy(int[] model, boolean min) {
		super();
		this.model = model;
		this.phase = new int[model.length + 1];
		if (min) {
			for (int i = 0; i < model.length; i++) {
				this.phase[i + 1] = model[i] >= 0 ? negLit(i + 1) : posLit(i + 1);
			}
		} else {
			for (int i = 0; i < model.length; i++) {
				this.phase[i + 1] = model[i] > 0 ? negLit(i + 1) : posLit(i + 1);
			}
		}
	}

	@Override
	public void updateVar(int p) {
	}

	@Override
	public void assignLiteral(int p) {
		final int var = var(p);
		if (model[var - 1] == 0) {
			this.phase[var] = p;
		}
	}

	@Override
	public void updateVarAtDecisionLevel(int q) {
	}

	public void init(int nlength) {
	}

	public void init(int var, int p) {
	}

	public int select(int var) {
		return this.phase[var];
	}

}