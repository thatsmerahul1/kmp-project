import SwiftUI

// MARK: - Atomic Text Field
// Basic text input atom following atomic design principles
// Matches Android AtomicTextField.kt implementation

struct AtomicTextField: View {
    
    // MARK: - Properties
    let title: String?
    let placeholder: String
    @Binding var text: String
    let isError: Bool
    let errorMessage: String?
    let enabled: Bool
    let readOnly: Bool
    let maxCharacters: Int?
    let keyboardType: UIKeyboardType
    let textContentType: UITextContentType?
    let onCommit: (() -> Void)?
    
    // MARK: - Initializer
    init(
        title: String? = nil,
        placeholder: String = "",
        text: Binding<String>,
        isError: Bool = false,
        errorMessage: String? = nil,
        enabled: Bool = true,
        readOnly: Bool = false,
        maxCharacters: Int? = nil,
        keyboardType: UIKeyboardType = .default,
        textContentType: UITextContentType? = nil,
        onCommit: (() -> Void)? = nil
    ) {
        self.title = title
        self.placeholder = placeholder
        self._text = text
        self.isError = isError
        self.errorMessage = errorMessage
        self.enabled = enabled
        self.readOnly = readOnly
        self.maxCharacters = maxCharacters
        self.keyboardType = keyboardType
        self.textContentType = textContentType
        self.onCommit = onCommit
    }
    
    // MARK: - Body
    var body: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.xs) {
            // Title Label
            if let title = title {
                Text(title)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(isError ? .atomicError : .atomicOnSurface)
            }
            
            // Text Field
            ZStack(alignment: .leading) {
                // Background
                RoundedRectangle(cornerRadius: AtomicShapes.inputField)
                    .fill(Color.atomicSurface)
                    .overlay(
                        RoundedRectangle(cornerRadius: AtomicShapes.inputField)
                            .stroke(
                                isError ? Color.atomicError :
                                    (enabled ? Color.atomicOutline : Color.atomicOnSurfaceVariant),
                                lineWidth: AtomicShapes.borderMedium
                            )
                    )
                
                // Text Input
                if readOnly {
                    Text(text.isEmpty ? placeholder : text)
                        .foregroundColor(text.isEmpty ? .atomicOnSurfaceVariant : .atomicOnSurface)
                        .atomicPaddingMD()
                } else {
                    TextField(placeholder, text: $text, onCommit: {
                        onCommit?()
                    })
                    .keyboardType(keyboardType)
                    .textContentType(textContentType)
                    .disabled(!enabled)
                    .foregroundColor(enabled ? .atomicOnSurface : .atomicOnSurfaceVariant)
                    .atomicPaddingMD()
                    .onChange(of: text) { newValue in
                        if let maxCharacters = maxCharacters,
                           newValue.count > maxCharacters {
                            text = String(newValue.prefix(maxCharacters))
                        }
                    }
                }
            }
            .frame(height: AtomicSpacing.minTouchTarget)
            
            // Error Message
            if isError, let errorMessage = errorMessage {
                Text(errorMessage)
                    .font(.system(size: 12))
                    .foregroundColor(.atomicError)
            }
            
            // Character Count (if max characters is set)
            if let maxCharacters = maxCharacters {
                HStack {
                    Spacer()
                    Text("\(text.count)/\(maxCharacters)")
                        .font(.system(size: 12))
                        .foregroundColor(.atomicOnSurfaceVariant)
                }
            }
        }
    }
}

// MARK: - Preview
struct AtomicTextField_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: AtomicSpacing.md) {
            AtomicTextField(
                title: "Email",
                placeholder: "Enter your email",
                text: .constant(""),
                keyboardType: .emailAddress
            )
            
            AtomicTextField(
                title: "Password",
                placeholder: "Enter password",
                text: .constant("sample text"),
                isError: true,
                errorMessage: "Password is too short"
            )
            
            AtomicTextField(
                title: "Disabled Field",
                placeholder: "Cannot edit",
                text: .constant("Disabled text"),
                enabled: false
            )
            
            AtomicTextField(
                title: "Limited Characters",
                placeholder: "Max 50 characters",
                text: .constant("Sample text with character limit"),
                maxCharacters: 50
            )
        }
        .atomicPadding()
        .previewLayout(.sizeThatFits)
    }
}