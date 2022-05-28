import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.List;

public class TestCustomSqlChange implements CustomSqlChange, CustomSqlRollback {

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
    public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if (randomNum>0) {
            return new RawSqlStatement[]{new RawSqlStatement("UPDATE As dfdfd set 1 =1 dfdf")};
        }
        return new RawSqlStatement[]{new RawSqlStatement("DELETE * FROM dD")};
    }

    @Override
    public SqlStatement[] generateRollbackStatements(Database database) throws CustomChangeException, RollbackImpossibleException {
        return new RawSqlStatement[]{new RawSqlStatement("UPDATE XX")};
    }

    @Override
    public String getConfirmationMessage() {
        return "Said Helloddxx";
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
