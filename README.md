# vinheria-agnelo

## Instruções

- Para chamar o `DatabaseConfig`:

```java
import java.sql.Connection;
import br.com.vinheriaagnello.config.DatabaseConfig;

Connection conn = DatabaseConfig.getDataSource().getConnection();
```
