# kafka-topic-manager

This service aims to let developers create topics as easily as we would
add tables to a database schema.

We also want to encourage ourselves to define schemas.

Microservices can't easily check the current "schema",
so we model this as an event log of Topic Declarations,
to which services are free to produce at every start.

To facilitate single-message produce like that this service
can also create REST endpoints.

While `auto.create.topics.enable=true` solves the immediate problem,
it does not encourage us to treat our topics like a database schema.

In early stages still. For scope control see [UnsupportedOperationException]()s and [TODOErrorHandling]()s.
