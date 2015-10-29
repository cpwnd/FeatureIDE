package de.ovgu.featureide.featurehouse.refactoring.pullUp;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class PullUpRefactoringWizard extends RefactoringWizard {
	
	/** The page name */
	private static final String PAGE_NAME= "PullUpMemberPage"; //$NON-NLS-1$

	private final PullUpRefactoring refactoring;


    public PullUpRefactoringWizard(final PullUpRefactoring refactoring) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
		this.refactoring = refactoring;
		setDefaultPageTitle(RefactoringMessages.PullUpWizard_defaultPageTitle);
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_REFACTOR_PULL_UP);
	}
    
	@Override
    protected void addUserInputPages(){		
		addPage(new PullUpMemberPage(PAGE_NAME, refactoring));
	}
	
}
