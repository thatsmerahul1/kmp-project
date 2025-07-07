/**
 * Coverage Badge Generation Gradle Script for KMP 2025
 * 
 * This script integrates with the existing Kover plugin to provide
 * automated coverage badge generation as part of the build process.
 * 
 * Usage:
 * - Apply this script to shared/build.gradle.kts
 * - Run `./gradlew generateCoverageBadges`
 * - Badges will be generated automatically after test execution
 */

import kotlinx.coroutines.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

// Coverage badge generation configuration
data class CoverageBadgeConfig(
    val thresholds: Map<String, Int> = mapOf(
        "excellent" to 90,
        "good" to 80, 
        "moderate" to 70,
        "poor" to 60
    ),
    val colors: Map<String, String> = mapOf(
        "excellent" to "brightgreen",
        "good" to "green",
        "moderate" to "yellow", 
        "poor" to "orange",
        "critical" to "red"
    ),
    val platforms: List<String> = listOf("android", "common", "ios"),
    val coverageTypes: List<String> = listOf("line", "branch", "instruction"),
    val badgeStyle: String = "flat-square",
    val logo: String = "kotlin"
)

// Badge generation task
tasks.register("generateCoverageBadges") {
    group = "verification"
    description = "Generate coverage badges from Kover XML reports"
    
    // Depend on Kover XML report generation
    dependsOn("koverXmlReport")
    
    // Input: Coverage XML reports
    val coverageReportDir = file("build/reports/kover")
    inputs.dir(coverageReportDir)
    
    // Output: Badge files
    val badgesDir = file("../.github/badges") 
    outputs.dir(badgesDir)
    
    doLast {
        val config = CoverageBadgeConfig()
        generateCoverageBadges(config, coverageReportDir, badgesDir)
    }
}

// Auto-generate badges after tests
tasks.named("koverXmlReport") {
    finalizedBy("generateCoverageBadges")
}

// Badge generation implementation
fun generateCoverageBadges(
    config: CoverageBadgeConfig,
    reportDir: File,
    outputDir: File
) {
    println("🎯 Generating coverage badges...")
    
    // Ensure output directory exists
    outputDir.mkdirs()
    
    // Generate overall coverage badges
    val overallReportFile = File(reportDir, "report.xml")
    if (overallReportFile.exists()) {
        config.coverageTypes.forEach { coverageType ->
            val percentage = parseCoveragePercentage(overallReportFile, coverageType)
            val badgeFile = File(outputDir, "coverage-$coverageType.svg")
            generateBadge(
                label = "coverage $coverageType",
                message = "$percentage%", 
                color = getBadgeColor(percentage, config),
                outputFile = badgeFile,
                config = config
            )
        }
    }
    
    // Generate platform-specific badges
    config.platforms.forEach { platform ->
        val platformReportFile = File(reportDir, "$platform/report.xml")
        val reportFile = if (platformReportFile.exists()) platformReportFile else overallReportFile
        
        if (reportFile.exists()) {
            config.coverageTypes.forEach { coverageType ->
                val percentage = parseCoveragePercentage(reportFile, coverageType)
                val badgeFile = File(outputDir, "coverage-$platform-$coverageType.svg")
                generateBadge(
                    label = "$platform $coverageType",
                    message = "$percentage%",
                    color = getBadgeColor(percentage, config), 
                    outputFile = badgeFile,
                    config = config
                )
            }
        }
    }
    
    println("✅ Coverage badges generated successfully!")
}

// Parse coverage percentage from XML report
fun parseCoveragePercentage(reportFile: File, coverageType: String): Int {
    try {
        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(reportFile)
        
        val counterType = when (coverageType) {
            "line" -> "LINE"
            "branch" -> "BRANCH" 
            "instruction" -> "INSTRUCTION"
            else -> return 0
        }
        
        val xpath = javax.xml.xpath.XPathFactory.newInstance().newXPath()
        val expression = "//counter[@type='$counterType']"
        val nodeList = xpath.evaluate(expression, doc, javax.xml.xpath.XPathConstants.NODESET) as org.w3c.dom.NodeList
        
        if (nodeList.length > 0) {
            val counter = nodeList.item(0) as Element
            val covered = counter.getAttribute("covered").toIntOrNull() ?: 0
            val missed = counter.getAttribute("missed").toIntOrNull() ?: 0
            val total = covered + missed
            
            return if (total > 0) (covered * 100) / total else 0
        }
        
        return 0
    } catch (e: Exception) {
        println("⚠️ Failed to parse coverage from ${reportFile.name}: ${e.message}")
        return 0
    }
}

// Get badge color based on percentage
fun getBadgeColor(percentage: Int, config: CoverageBadgeConfig): String {
    return when {
        percentage >= config.thresholds["excellent"]!! -> config.colors["excellent"]!!
        percentage >= config.thresholds["good"]!! -> config.colors["good"]!!
        percentage >= config.thresholds["moderate"]!! -> config.colors["moderate"]!!
        percentage >= config.thresholds["poor"]!! -> config.colors["poor"]!!
        else -> config.colors["critical"]!!
    }
}

// Generate individual badge
fun generateBadge(
    label: String,
    message: String, 
    color: String,
    outputFile: File,
    config: CoverageBadgeConfig
) {
    try {
        val encodedLabel = label.replace(" ", "%20").replace("-", "--")
        val encodedMessage = message.replace(" ", "%20")
        val badgeUrl = "https://img.shields.io/badge/$encodedLabel-$encodedMessage-$color" +
                "?style=${config.badgeStyle}&logo=${config.logo}"
        
        println("📍 Generating badge: $label -> $message ($color)")
        
        // Download badge SVG
        val connection = URL(badgeUrl).openConnection()
        connection.setRequestProperty("User-Agent", "Coverage-Badge-Generator/1.0")
        
        Files.copy(connection.getInputStream(), outputFile.toPath())
        
        println("✅ Badge saved: ${outputFile.name}")
    } catch (e: Exception) {
        println("❌ Failed to generate badge for $label: ${e.message}")
    }
}

// Coverage validation task
tasks.register("validateCoverage") {
    group = "verification"
    description = "Validate coverage against defined thresholds"
    
    dependsOn("koverXmlReport")
    
    doLast {
        val config = CoverageBadgeConfig()
        val reportFile = file("build/reports/kover/report.xml")
        
        if (reportFile.exists()) {
            val results = config.coverageTypes.map { coverageType ->
                val percentage = parseCoveragePercentage(reportFile, coverageType)
                coverageType to percentage
            }
            
            println("📊 Coverage Summary:")
            results.forEach { (type, percentage) ->
                val status = when {
                    percentage >= config.thresholds["excellent"]!! -> "🟢 EXCELLENT"
                    percentage >= config.thresholds["good"]!! -> "🟡 GOOD"
                    percentage >= config.thresholds["moderate"]!! -> "🟠 MODERATE" 
                    percentage >= config.thresholds["poor"]!! -> "🔴 POOR"
                    else -> "❌ CRITICAL"
                }
                println("  $type: $percentage% $status")
            }
            
            // Check if any coverage type is below threshold
            val failedChecks = results.filter { (_, percentage) -> 
                percentage < config.thresholds["poor"]!! 
            }
            
            if (failedChecks.isNotEmpty()) {
                println("⚠️ Coverage validation warnings:")
                failedChecks.forEach { (type, percentage) ->
                    println("  - $type coverage ($percentage%) is below threshold (${config.thresholds["poor"]}%)")
                }
            } else {
                println("🎉 All coverage thresholds passed!")
            }
        } else {
            println("❌ Coverage report not found: ${reportFile.path}")
        }
    }
}

// Update README task
tasks.register("updateReadmeWithBadges") {
    group = "documentation"
    description = "Update README.md with generated coverage badges"
    
    dependsOn("generateCoverageBadges")
    
    doLast {
        val readmeFile = file("../README.md")
        val config = CoverageBadgeConfig()
        
        if (readmeFile.exists()) {
            updateReadmeWithCoverageBadges(readmeFile, config)
            println("📝 README.md updated with coverage badges")
        } else {
            println("⚠️ README.md not found: ${readmeFile.path}")
        }
    }
}

// README update implementation
fun updateReadmeWithCoverageBadges(readmeFile: File, config: CoverageBadgeConfig) {
    val content = readmeFile.readText()
    
    val badgesSection = buildString {
        appendLine("## 📊 Coverage Badges")
        appendLine()
        
        // Overall badges
        appendLine("### Overall Coverage")
        config.coverageTypes.forEach { type ->
            appendLine("![${type.capitalize()} Coverage](.github/badges/coverage-$type.svg)")
        }
        appendLine()
        
        // Platform-specific badges
        appendLine("### Platform-Specific Coverage")
        config.platforms.forEach { platform ->
            appendLine()
            appendLine("#### ${platform.capitalize()}")
            config.coverageTypes.forEach { type ->
                appendLine("![${platform.capitalize()} ${type.capitalize()} Coverage](.github/badges/coverage-$platform-$type.svg)")
            }
        }
        appendLine()
        appendLine("---")
        appendLine()
    }
    
    // Replace or insert badges section
    val updatedContent = if (content.contains("## 📊 Coverage Badges")) {
        // Replace existing section
        content.replace(
            Regex("## 📊 Coverage Badges.*?^---$", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)),
            badgesSection.trimEnd()
        )
    } else {
        // Insert after first heading
        val lines = content.lines().toMutableList()
        if (lines.isNotEmpty()) {
            lines.add(1, "")
            lines.add(2, badgesSection.trimEnd())
            lines.joinToString("\n")
        } else {
            content
        }
    }
    
    readmeFile.writeText(updatedContent)
}

// Comprehensive coverage workflow task
tasks.register("coverageWorkflow") {
    group = "verification"
    description = "Complete coverage workflow: test, generate badges, validate, and update docs"
    
    dependsOn("test", "koverXmlReport", "generateCoverageBadges", "validateCoverage", "updateReadmeWithBadges")
    
    doLast {
        println("🎯 Coverage workflow completed successfully!")
        println("📂 Badges directory: ${file("../.github/badges").absolutePath}")
        println("📄 README.md updated with coverage information")
    }
}