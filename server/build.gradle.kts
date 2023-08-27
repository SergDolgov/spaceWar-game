plugins {
    id("java")
}
group = "com.badlogicgames.gdx"
version = "1.9.0"
group = "com.company.gdx"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}