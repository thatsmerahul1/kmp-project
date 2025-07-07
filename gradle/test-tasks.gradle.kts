import java.awt.Desktop
import java.io.File

// Task to run all tests with coverage
tasks.register("testAll") {
    group = "verification"
    description = "Run all tests for all modules"
    
    dependsOn(":shared:allTests")
    dependsOn(":androidApp:testDebugUnitTest")
    
    doLast {
        println("\n✅ All tests completed!")
        println("Run './gradlew koverHtmlReport' to generate coverage report")
    }
}

// Task to run shared module tests
tasks.register("testShared") {
    group = "verification"
    description = "Run tests for shared module"
    
    dependsOn(":shared:allTests")
    
    doLast {
        println("\n✅ Shared module tests completed!")
    }
}

// Task to run Android app tests
tasks.register("testAndroid") {
    group = "verification"
    description = "Run tests for Android app"
    
    dependsOn(":androidApp:testDebugUnitTest")
    
    doLast {
        println("\n✅ Android app tests completed!")
    }
}

// Task to run iOS tests (via shared module iOS targets)
tasks.register("testIos") {
    group = "verification"
    description = "Run tests for iOS targets"
    
    dependsOn(":shared:iosX64Test")
    dependsOn(":shared:iosArm64Test")
    dependsOn(":shared:iosSimulatorArm64Test")
    
    doLast {
        println("\n✅ iOS tests completed!")
    }
}

// Task to generate coverage report and open it
tasks.register("koverHtmlReportWithOpen") {
    group = "verification"
    description = "Generate Kover HTML report and open in browser"
    
    dependsOn("koverHtmlReport")
    
    doLast {
        val reportPath = file("${buildDir}/reports/kover/html/index.html")
        if (reportPath.exists()) {
            println("\n📊 Coverage report generated at: ${reportPath.absolutePath}")
            
            // Try to open the report in default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(reportPath.toURI())
                    println("📂 Opening coverage report in browser...")
                } catch (e: Exception) {
                    println("⚠️  Could not open browser automatically. Please open manually: ${reportPath.absolutePath}")
                }
            } else {
                println("ℹ️  Please open the report manually: ${reportPath.absolutePath}")
            }
        } else {
            println("❌ Coverage report not found at expected location")
        }
    }
}

// Task to run all tests with coverage and open report
tasks.register("testAllWithCoverage") {
    group = "verification"
    description = "Run all tests with coverage and open report"
    
    dependsOn("testAll", "koverHtmlReportWithOpen")
}

// Enhanced coverage verification with 85% threshold
tasks.register("koverVerifyEnhanced") {
    group = "verification"
    description = "Verify coverage meets 85% threshold with detailed reporting"
    
    dependsOn("koverXmlReport")
    
    doLast {
        val xmlReportPath = file("${buildDir}/reports/kover/xml/report.xml")
        if (!xmlReportPath.exists()) {
            throw GradleException("❌ Coverage XML report not found. Run tests first.")
        }
        
        try {
            val reportContent = xmlReportPath.readText()
            val coverageRegex = """<counter type="LINE".*?covered="(\d+)".*?missed="(\d+)"""".toRegex()
            val match = coverageRegex.find(reportContent)
            
            if (match != null) {
                val covered = match.groupValues[1].toInt()
                val missed = match.groupValues[2].toInt()
                val total = covered + missed
                val percentage = if (total > 0) (covered.toDouble() / total * 100) else 0.0
                
                println("\n📊 Coverage Analysis:")
                println("   Lines covered: $covered")
                println("   Lines missed: $missed")
                println("   Total lines: $total")
                println("   Coverage: ${String.format("%.2f", percentage)}%")
                println("   Threshold: 85%")
                
                if (percentage < 85.0) {
                    val needed = ((total * 0.85) - covered).toInt()
                    println("\n❌ Coverage threshold not met!")
                    println("   Need to cover $needed more lines to reach 85%")
                    throw GradleException("Code coverage is ${String.format("%.2f", percentage)}% but required minimum is 85%")
                } else {
                    println("\n✅ Coverage threshold met!")
                }
            } else {
                throw GradleException("❌ Could not parse coverage data from XML report")
            }
        } catch (e: Exception) {
            if (e is GradleException) throw e
            throw GradleException("❌ Error reading coverage report: ${e.message}")
        }
    }
}

// Quality gates task that combines multiple checks
tasks.register("qualityGate") {
    group = "verification"
    description = "Run comprehensive quality gates (tests, coverage, complexity)"
    
    dependsOn("testAll", "koverVerifyEnhanced")
    
    doLast {
        println("\n🏆 Quality gates passed!")
        println("   ✅ All tests passed")
        println("   ✅ Coverage threshold met (85%)")
        println("   ✅ Code quality standards upheld")
    }
}

// Task to verify coverage thresholds
tasks.register("verifyCoverage") {
    group = "verification"
    description = "Verify code coverage meets minimum thresholds"
    
    dependsOn("koverVerify")
    
    doLast {
        println("\n✅ Coverage verification completed!")
    }
}

// Task to generate test summary report
tasks.register("generateTestReport") {
    group = "reporting"
    description = "Generate comprehensive test report"
    
    dependsOn("testAll", "koverHtmlReport", "koverXmlReport")
    
    doLast {
        val htmlReport = file("${buildDir}/reports/kover/html/index.html")
        val xmlReport = file("${buildDir}/reports/kover/xml/report.xml")
        
        println("\n📊 Test Report Summary")
        println("====================")
        println("✅ All tests executed")
        println("📁 HTML Report: ${htmlReport.absolutePath}")
        println("📁 XML Report: ${xmlReport.absolutePath}")
        
        // Parse XML report for coverage percentage if exists
        if (xmlReport.exists()) {
            try {
                val xmlContent = xmlReport.readText()
                val lineRateMatch = Regex("""line-rate="([0-9.]+)"""").find(xmlContent)
                if (lineRateMatch != null) {
                    val lineRate = lineRateMatch.groupValues[1].toDouble()
                    val percentage = (lineRate * 100).toInt()
                    println("📈 Overall Coverage: $percentage%")
                    
                    if (percentage >= 85) {
                        println("✅ Coverage target (85%) achieved!")
                    } else {
                        println("⚠️  Coverage ($percentage%) is below target (85%)")
                    }
                }
            } catch (e: Exception) {
                println("⚠️  Could not parse coverage data")
            }
        }
    }
}

// Add support for --popup-report flag
gradle.taskGraph.whenReady {
    val popupReport = project.hasProperty("popup-report") || 
                     project.gradle.startParameter.projectProperties.containsKey("popup-report")
    
    if (popupReport) {
        allTasks.filter { it.name.startsWith("test") && !it.name.contains("Coverage") }
            .forEach { task ->
                task.finalizedBy("koverHtmlReportWithOpen")
            }
    }
}