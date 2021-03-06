import liquibase.Scope;
import liquibase.change.custom.CustomTaskChange;
import liquibase.change.custom.CustomTaskRollback;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class TestChange implements CustomTaskChange, CustomTaskRollback {
    private String helloTo;
    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    private ResourceAccessor resourceAccessor;
    public String getHelloTo() {
        return helloTo;
    }
    public void setHelloTo(String helloTo) {
        this.helloTo = helloTo;
    }
    @Override
    public void execute(Database database) throws CustomChangeException {
        int a = 1;
        Scope.getCurrentScope().getLog(getClass()).info("Hello ddd"+getHelloTo());
    }
    @Override
    public void rollback(Database database) throws CustomChangeException, RollbackImpossibleException {
        Scope.getCurrentScope().getLog(getClass()).info("Goodbye "+getHelloTo());
    }
    @Override
    public String getConfirmationMessage() {
        return "Said Hello";
    }
    @Override
    public void setUp() throws SetupException {
        ;
    }
    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }
    @Override
    public ValidationErrors validate(Database database) {
        return new ValidationErrors();
    }
}
