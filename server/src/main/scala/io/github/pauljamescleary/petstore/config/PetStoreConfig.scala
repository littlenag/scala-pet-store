package io.github.pauljamescleary.petstore.config

final case class ServerConfig(host: String, port: Int)
final case class MailerConfig(host: String, port: Int, user:String, password:String, startTls:Option[Boolean], mock:Boolean)
final case class PetStoreConfig(baseUrl: String, db: DatabaseConfig, server: ServerConfig, mailer: MailerConfig)
