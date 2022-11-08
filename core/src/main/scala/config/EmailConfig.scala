package config

case class EmailConfig(
    host: String,
    port: Int,
    sender: String,
    password: String
)
