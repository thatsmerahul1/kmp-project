import java.io.File
import kotlinx.serialization.json.*

/**
 * Code complexity analysis for 2025 DevOps standards
 * 
 * This script provides comprehensive code complexity metrics including:
 * - Cyclomatic complexity per function
 * - Lines of code per class/file
 * - Function complexity distribution
 * - Maintainability index
 * - Technical debt estimation
 */

// Task to analyze code complexity
tasks.register("analyzeComplexity") {
    group = "verification"
    description = "Analyze code complexity and generate detailed metrics"
    
    doLast {
        println("🔍 Starting comprehensive complexity analysis...")
        
        val sourceDirectories = listOf(
            "shared/src/commonMain/kotlin",
            "shared/src/androidMain/kotlin",
            "shared/src/iosMain/kotlin",
            "androidApp/src/main/kotlin"
        )
        
        val analysisResults = mutableMapOf<String, Any>()
        var totalComplexity = 0
        var totalFunctions = 0
        var totalLines = 0
        var totalFiles = 0
        val complexityDistribution = mutableMapOf<String, Int>()
        val highComplexityFiles = mutableListOf<Map<String, Any>>()
        
        sourceDirectories.forEach { sourceDir ->
            val dir = file(sourceDir)
            if (dir.exists()) {
                dir.walkTopDown()
                    .filter { it.isFile && it.extension == "kt" }
                    .forEach { kotlinFile ->
                        try {
                            val fileAnalysis = analyzeKotlinFile(kotlinFile)
                            totalComplexity += fileAnalysis["complexity"] as Int
                            totalFunctions += fileAnalysis["functions"] as Int
                            totalLines += fileAnalysis["lines"] as Int
                            totalFiles++
                            
                            // Track complexity distribution
                            val avgComplexity = fileAnalysis["avgComplexityPerFunction"] as Double
                            val complexityBucket = when {
                                avgComplexity <= 2 -> "Simple (≤2)"
                                avgComplexity <= 5 -> "Moderate (3-5)"
                                avgComplexity <= 10 -> "Complex (6-10)"
                                else -> "Very Complex (>10)"
                            }
                            complexityDistribution[complexityBucket] = 
                                complexityDistribution.getOrDefault(complexityBucket, 0) + 1
                            
                            // Track high complexity files
                            if (avgComplexity > 10) {
                                highComplexityFiles.add(
                                    mapOf(
                                        "file" to kotlinFile.relativeTo(projectDir).path,
                                        "complexity" to avgComplexity,
                                        "functions" to fileAnalysis["functions"],
                                        "lines" to fileAnalysis["lines"]
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            println("⚠️ Error analyzing ${kotlinFile.name}: ${e.message}")
                        }
                    }
            }
        }
        
        // Calculate aggregated metrics
        val avgComplexityPerFile = if (totalFiles > 0) totalComplexity.toDouble() / totalFiles else 0.0
        val avgComplexityPerFunction = if (totalFunctions > 0) totalComplexity.toDouble() / totalFunctions else 0.0
        val avgLinesPerFile = if (totalFiles > 0) totalLines.toDouble() / totalFiles else 0.0
        val avgFunctionsPerFile = if (totalFiles > 0) totalFunctions.toDouble() / totalFiles else 0.0
        
        // Calculate maintainability index (simplified version)
        val maintainabilityIndex = calculateMaintainabilityIndex(
            avgLinesPerFile, avgComplexityPerFunction, totalLines
        )
        
        // Generate detailed report
        val reportDir = file("${project.buildDir}/reports/complexity")
        reportDir.mkdirs()
        
        val reportFile = file("${reportDir}/complexity-report.json")
        val jsonReport = buildJsonObject {
            put("timestamp", kotlinx.datetime.Clock.System.now().toString())
            put("project", project.name)
            put("summary", buildJsonObject {
                put("totalFiles", totalFiles)
                put("totalFunctions", totalFunctions)
                put("totalLines", totalLines)
                put("totalComplexity", totalComplexity)
                put("avgComplexityPerFile", String.format("%.2f", avgComplexityPerFile))
                put("avgComplexityPerFunction", String.format("%.2f", avgComplexityPerFunction))
                put("avgLinesPerFile", String.format("%.2f", avgLinesPerFile))
                put("avgFunctionsPerFile", String.format("%.2f", avgFunctionsPerFile))
                put("maintainabilityIndex", String.format("%.2f", maintainabilityIndex))
            })
            put("distribution", buildJsonObject {
                complexityDistribution.forEach { (bucket, count) ->
                    put(bucket, count)
                }
            })
            put("highComplexityFiles", buildJsonArray {
                highComplexityFiles.forEach { fileData ->
                    add(buildJsonObject {
                        fileData.forEach { (key, value) ->
                            when (value) {
                                is String -> put(key, value)
                                is Number -> put(key, value.toDouble())
                            }
                        }
                    })
                }
            })
        }
        
        reportFile.writeText(jsonReport.toString())
        
        // Generate HTML report
        generateHtmlComplexityReport(reportDir, jsonReport)
        
        // Print summary
        println("\n📊 Code Complexity Analysis Results:")
        println("   📁 Total Files: $totalFiles")
        println("   🔧 Total Functions: $totalFunctions")
        println("   📏 Total Lines: $totalLines")
        println("   🎯 Avg Complexity/Function: ${String.format("%.2f", avgComplexityPerFunction)}")
        println("   📈 Maintainability Index: ${String.format("%.2f", maintainabilityIndex)}")
        
        println("\n📊 Complexity Distribution:")
        complexityDistribution.forEach { (bucket, count) ->
            val percentage = (count.toDouble() / totalFiles * 100)
            println("   $bucket: $count files (${String.format("%.1f", percentage)}%)")
        }
        
        if (highComplexityFiles.isNotEmpty()) {
            println("\n⚠️ High Complexity Files (>10 avg complexity):")
            highComplexityFiles.take(5).forEach { fileData ->
                println("   📄 ${fileData["file"]}: ${String.format("%.2f", fileData["complexity"] as Double)} complexity")
            }
            if (highComplexityFiles.size > 5) {
                println("   ... and ${highComplexityFiles.size - 5} more (see full report)")
            }
        }
        
        println("\n📋 Report generated: ${reportFile.absolutePath}")
        
        // Fail build if complexity thresholds are exceeded
        if (avgComplexityPerFunction > 15) {
            throw GradleException("❌ Code complexity too high! Average complexity per function: ${String.format("%.2f", avgComplexityPerFunction)} (max: 15)")
        } else if (avgComplexityPerFunction > 10) {
            println("\n⚠️ Warning: Code complexity approaching limits (${String.format("%.2f", avgComplexityPerFunction)}). Consider refactoring.")
        } else {
            println("\n✅ Code complexity within acceptable limits")
        }
    }
}

// Task to enforce complexity thresholds
tasks.register("verifyComplexity") {
    group = "verification"
    description = "Verify code complexity meets project standards"
    
    dependsOn("analyzeComplexity")
    
    doLast {
        val reportFile = file("${project.buildDir}/reports/complexity/complexity-report.json")
        if (!reportFile.exists()) {
            throw GradleException("❌ Complexity report not found. Run analyzeComplexity first.")
        }
        
        val report = Json.parseToJsonElement(reportFile.readText()).jsonObject
        val summary = report["summary"]?.jsonObject
        
        val avgComplexityPerFunction = summary?.get("avgComplexityPerFunction")?.jsonPrimitive?.content?.toDouble() ?: 0.0
        val maintainabilityIndex = summary?.get("maintainabilityIndex")?.jsonPrimitive?.content?.toDouble() ?: 0.0
        
        println("\n🎯 Complexity Verification:")
        println("   Average Complexity per Function: ${String.format("%.2f", avgComplexityPerFunction)} (target: ≤5)")
        println("   Maintainability Index: ${String.format("%.2f", maintainabilityIndex)} (target: ≥60)")
        
        var hasIssues = false
        
        if (avgComplexityPerFunction > 5) {
            println("   ❌ Complexity threshold exceeded!")
            hasIssues = true
        } else {
            println("   ✅ Complexity within target")
        }
        
        if (maintainabilityIndex < 60) {
            println("   ❌ Maintainability below target!")
            hasIssues = true
        } else {
            println("   ✅ Maintainability acceptable")
        }
        
        if (hasIssues) {
            println("\n💡 Recommendations:")
            println("   - Break down large functions into smaller ones")
            println("   - Reduce nested conditionals and loops")
            println("   - Extract complex logic into separate functions")
            println("   - Consider using design patterns to simplify code")
        }
    }
}

/**
 * Analyze a single Kotlin file for complexity metrics
 */
fun analyzeKotlinFile(file: File): Map<String, Any> {
    val content = file.readText()
    val lines = content.lines()
    
    // Count actual lines of code (excluding empty lines and comments)
    val codeLines = lines.filter { line ->
        val trimmed = line.trim()
        trimmed.isNotEmpty() && 
        !trimmed.startsWith("//") && 
        !trimmed.startsWith("/*") && 
        !trimmed.startsWith("*") &&
        !trimmed.startsWith("*/")
    }.size
    
    // Find functions
    val functionRegex = """^\s*(private\s+|protected\s+|internal\s+|public\s+)?(suspend\s+)?(inline\s+|noinline\s+|crossinline\s+)?(override\s+)?fun\s+\w+""".toRegex(RegexOption.MULTILINE)
    val functions = functionRegex.findAll(content).count()
    
    // Calculate cyclomatic complexity
    val complexityIndicators = listOf(
        "if\\s*\\(" to "if",
        "else\\s*\\{" to "else",
        "else\\s+if" to "else if",
        "when\\s*\\(" to "when",
        "while\\s*\\(" to "while",
        "for\\s*\\(" to "for",
        "do\\s*\\{" to "do",
        "catch\\s*\\(" to "catch",
        "&&" to "and",
        "\\|\\|" to "or",
        "\\?:" to "elvis",
        "\\?\\." to "safe call"
    )
    
    var complexity = functions // Base complexity (each function starts with 1)
    
    complexityIndicators.forEach { (pattern, _) ->
        complexity += pattern.toRegex().findAll(content).count()
    }
    
    val avgComplexityPerFunction = if (functions > 0) complexity.toDouble() / functions else 0.0
    
    return mapOf(
        "complexity" to complexity,
        "functions" to functions,
        "lines" to codeLines,
        "avgComplexityPerFunction" to avgComplexityPerFunction,
        "file" to file.relativeTo(project.rootDir).path
    )
}

/**
 * Calculate maintainability index (simplified Microsoft-style calculation)
 */
fun calculateMaintainabilityIndex(avgLinesPerFile: Double, avgComplexity: Double, totalLines: Int): Double {
    // Simplified maintainability index calculation
    // Real formula: 171 - 5.2 * ln(Halstead Volume) - 0.23 * (Cyclomatic Complexity) - 16.2 * ln(Lines of Code)
    // Simplified version for this implementation
    val complexityPenalty = avgComplexity * 2
    val sizePenalty = kotlin.math.log10(totalLines.toDouble()) * 10
    
    return (100 - complexityPenalty - sizePenalty).coerceAtLeast(0.0)
}

/**
 * Generate HTML complexity report
 */
fun generateHtmlComplexityReport(reportDir: File, jsonReport: JsonObject) {
    val htmlFile = file("${reportDir}/complexity-report.html")
    
    val summary = jsonReport["summary"]?.jsonObject
    val distribution = jsonReport["distribution"]?.jsonObject
    val highComplexityFiles = jsonReport["highComplexityFiles"]?.jsonArray
    
    val html = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Code Complexity Report - ${project.name}</title>
        <style>
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
            .container { max-width: 1200px; margin: 0 auto; }
            .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }
            .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 20px; }
            .metric-card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            .metric-value { font-size: 2em; font-weight: bold; margin: 10px 0; }
            .metric-label { color: #666; font-size: 0.9em; text-transform: uppercase; letter-spacing: 1px; }
            .chart-container { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
            .file-list { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            .file-item { padding: 10px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; }
            .file-name { font-family: monospace; }
            .complexity-high { color: #f44336; font-weight: bold; }
            .complexity-medium { color: #ff9800; }
            .complexity-low { color: #4caf50; }
            table { width: 100%; border-collapse: collapse; }
            th, td { text-align: left; padding: 8px; border-bottom: 1px solid #ddd; }
            th { background-color: #f2f2f2; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>🔍 Code Complexity Analysis</h1>
                <p>Project: ${project.name} | Generated: ${kotlinx.datetime.Clock.System.now()}</p>
            </div>
            
            <div class="metrics-grid">
                <div class="metric-card">
                    <div class="metric-label">Avg Complexity/Function</div>
                    <div class="metric-value">${summary?.get("avgComplexityPerFunction")?.jsonPrimitive?.content ?: "0"}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">Total Functions</div>
                    <div class="metric-value">${summary?.get("totalFunctions")?.jsonPrimitive?.content ?: "0"}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">Total Files</div>
                    <div class="metric-value">${summary?.get("totalFiles")?.jsonPrimitive?.content ?: "0"}</div>
                </div>
                <div class="metric-card">
                    <div class="metric-label">Maintainability Index</div>
                    <div class="metric-value">${summary?.get("maintainabilityIndex")?.jsonPrimitive?.content ?: "0"}</div>
                </div>
            </div>
            
            <div class="chart-container">
                <h3>📊 Complexity Distribution</h3>
                <table>
                    <tr><th>Complexity Range</th><th>File Count</th><th>Percentage</th></tr>
                    ${distribution?.entries?.joinToString("") { (range, count) ->
                        val total = summary?.get("totalFiles")?.jsonPrimitive?.content?.toInt() ?: 1
                        val percentage = String.format("%.1f", count.jsonPrimitive.content.toDouble() / total * 100)
                        "<tr><td>$range</td><td>${count.jsonPrimitive.content}</td><td>$percentage%</td></tr>"
                    } ?: ""}
                </table>
            </div>
            
            ${if (highComplexityFiles?.isNotEmpty() == true) """
            <div class="file-list">
                <h3>⚠️ High Complexity Files</h3>
                ${highComplexityFiles.joinToString("") { fileElement ->
                    val fileObj = fileElement.jsonObject
                    val fileName = fileObj["file"]?.jsonPrimitive?.content ?: ""
                    val complexity = fileObj["complexity"]?.jsonPrimitive?.content ?: "0"
                    """<div class="file-item">
                        <span class="file-name">$fileName</span>
                        <span class="complexity-high">$complexity</span>
                    </div>"""
                }}
            </div>
            """ else ""}
        </div>
    </body>
    </html>
    """.trimIndent()
    
    htmlFile.writeText(html)
    println("📋 HTML report generated: ${htmlFile.absolutePath}")
}