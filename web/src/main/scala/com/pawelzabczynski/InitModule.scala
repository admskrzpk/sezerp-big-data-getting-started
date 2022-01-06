package com.pawelzabczynski

import com.pawelzabczynski.config.ConfigModule
import com.pawelzabczynski.infrastructure.DataBase

trait InitModule extends ConfigModule {

  lazy val dataBase = new DataBase(config.db)

}
