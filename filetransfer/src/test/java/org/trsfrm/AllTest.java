package org.trsfrm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.trsfrm.file.parser.JsonFileParserTest;
import org.trsfrm.file.parser.KafkaFileProducerTest;
import org.trsfrm.file.parser.TxtFileParserTest;
import org.trsfrm.file.parser.XmlFileParserTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   XmlFileParserTest.class,
   JsonFileParserTest.class,
   TxtFileParserTest.class,
   KafkaFileProducerTest.class
})
public class AllTest {

}
