#!/bin/bash
HOME=`pwd`
java -Dro.axonsoft.internship.jcis.url=file:config/jcis.yml -jar $HOME/lib/internship-app-*.jar $1 $2


