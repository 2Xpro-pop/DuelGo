# DuelGo
This is a project that uses C# as the client and Java as the server. Why am I not using ASP.NET? Because my university has instructed me to use Java.

## ServerCoffeeEdition
This is a library for creating game servers. My library helps create a server that uses TCP connections. It utilizes the chain of responsibility design pattern to open and close connections and provides a default implementation for a command handler (which is similar to controllers). It also uses Guice for dependency injection.

More information in [README.md](ServerCoffeeEdition/README.md)