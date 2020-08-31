java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    disableAutoTargetJvm()
}

plugins {
    id("org.beryx.jlink") version("2.21.2")
    id( "org.ysb33r.java.modulehelper") version("0.9.0")
//    id("com.github.johnrengelman.shadow") version "5.2.0"
}


tasks {
    val taskScope = this
    val jar: Jar by taskScope
    jar.apply {
        manifest.attributes["Implementation-Version"] = project.version
        manifest.attributes["Implementation-Vendor"] = "New Relic, Inc"
    }
}

extraJavaModules {
//    module("slf4j-api-1.7.30.jar","org.slf4j","1.7.30") {
//        exports("org.slf4j")
//        exports("org.slf4j.event")
//    }
//    module("gson-2.8.0.jar","com.google.code.gson","2.8.0") {
//        exports("com.google.gson")
//    }
    module("telemetry-all-0.8.0-SNAPSHOT.jar", "com.newrelic.telemetry", "0.8.0-SNAPSHOT") {
        exports("com.newrelic.telemetry")
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.newrelic"
            artifactId = "jfr-mappers"
            version = version
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("Library of mappers to turn JFR RecordedEvents into New Relic telemetry")
                url.set("https://github.com/newrelic/newrelic-jfr-core")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("newrelic")
                        name.set("New Relic")
                        email.set("opensource@newrelic.com")
                    }
                }
                scm {
                    url.set("git@github.com:newrelic/newrelic-jfr-core.git")
                    connection.set("scm:git:git@github.com:newrelic/newrelic-jfr-core.git")
                }
            }
        }
    }

}

signing {
    val signingKey : String? by project
    val signingKeyId: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    this.sign(publishing.publications["maven"])
}
