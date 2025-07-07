import SwiftUI

// MARK: - Atomic Search Field
// Search-optimized input field atom
// Matches Android AtomicSearchField.kt implementation

struct AtomicSearchField: View {
    
    // MARK: - Properties
    let placeholder: String
    @Binding var text: String
    let enabled: Bool
    let onSearch: (() -> Void)?
    let onClear: (() -> Void)?
    
    @FocusState private var isFocused: Bool
    
    // MARK: - Initializer
    init(
        placeholder: String = "Search...",
        text: Binding<String>,
        enabled: Bool = true,
        onSearch: (() -> Void)? = nil,
        onClear: (() -> Void)? = nil
    ) {
        self.placeholder = placeholder
        self._text = text
        self.enabled = enabled
        self.onSearch = onSearch
        self.onClear = onClear
    }
    
    // MARK: - Body
    var body: some View {
        HStack(spacing: AtomicSpacing.sm) {
            // Search Icon
            Image(systemName: "magnifyingglass")
                .foregroundColor(.atomicOnSurfaceVariant)
                .frame(width: AtomicSpacing.iconSize, height: AtomicSpacing.iconSize)
            
            // Search Text Field
            TextField(placeholder, text: $text)
                .focused($isFocused)
                .keyboardType(.default)
                .textContentType(.none)
                .disabled(!enabled)
                .foregroundColor(enabled ? .atomicOnSurface : .atomicOnSurfaceVariant)
                .onSubmit {
                    onSearch?()
                    isFocused = false
                }
            
            // Clear Button
            if !text.isEmpty {
                Button(action: {
                    text = ""
                    onClear?()
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.atomicOnSurfaceVariant)
                        .frame(width: AtomicSpacing.iconSize, height: AtomicSpacing.iconSize)
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .atomicPaddingMD()
        .frame(height: AtomicSpacing.minTouchTarget)
        .background(Color.atomicSurface)
        .overlay(
            RoundedRectangle(cornerRadius: AtomicShapes.searchField)
                .stroke(
                    isFocused ? Color.atomicPrimary : Color.atomicOutline,
                    lineWidth: isFocused ? 2 : AtomicShapes.borderMedium
                )
        )
        .cornerRadius(AtomicShapes.searchField)
        .animation(.easeInOut(duration: 0.2), value: isFocused)
    }
}

// MARK: - Search Field with Search Button
struct AtomicSearchFieldWithButton: View {
    
    // MARK: - Properties
    let placeholder: String
    @Binding var text: String
    let enabled: Bool
    let isLoading: Bool
    let searchButtonText: String
    let onSearch: (() -> Void)?
    
    // MARK: - Initializer
    init(
        placeholder: String = "Search...",
        text: Binding<String>,
        enabled: Bool = true,
        isLoading: Bool = false,
        searchButtonText: String = "Search",
        onSearch: (() -> Void)? = nil
    ) {
        self.placeholder = placeholder
        self._text = text
        self.enabled = enabled
        self.isLoading = isLoading
        self.searchButtonText = searchButtonText
        self.onSearch = onSearch
    }
    
    // MARK: - Body
    var body: some View {
        VStack(spacing: AtomicSpacing.sm) {
            AtomicSearchField(
                placeholder: placeholder,
                text: $text,
                enabled: enabled && !isLoading,
                onSearch: onSearch
            )
            
            // Search Button
            Button(action: {
                onSearch?()
            }) {
                HStack {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .atomicOnPrimary))
                            .scaleEffect(0.8)
                    }
                    
                    Text(searchButtonText)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.atomicOnPrimary)
                }
                .frame(maxWidth: .infinity)
                .frame(height: AtomicSpacing.buttonHeight)
                .background(
                    (enabled && !text.isEmpty && !isLoading) ?
                        Color.atomicPrimary : Color.atomicOnSurfaceVariant
                )
                .cornerRadius(AtomicShapes.button)
            }
            .disabled(!enabled || text.isEmpty || isLoading)
            .buttonStyle(PlainButtonStyle())
        }
    }
}

// MARK: - Preview
struct AtomicSearchField_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: AtomicSpacing.lg) {
            AtomicSearchField(
                placeholder: "Search locations...",
                text: .constant("")
            )
            
            AtomicSearchField(
                placeholder: "Search weather...",
                text: .constant("New York")
            )
            
            AtomicSearchFieldWithButton(
                placeholder: "Search locations...",
                text: .constant("San Francisco"),
                searchButtonText: "Find Weather"
            )
            
            AtomicSearchFieldWithButton(
                placeholder: "Searching...",
                text: .constant("London"),
                isLoading: true,
                searchButtonText: "Searching..."
            )
        }
        .atomicPadding()
        .previewLayout(.sizeThatFits)
    }
}