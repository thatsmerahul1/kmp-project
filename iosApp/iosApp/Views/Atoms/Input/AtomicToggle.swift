import SwiftUI

// MARK: - Atomic Toggle (Switch)
// Toggle/Switch atom component
// Matches Android AtomicSwitch.kt implementation

struct AtomicToggle: View {
    
    // MARK: - Properties
    let label: String?
    @Binding var isOn: Bool
    let enabled: Bool
    let labelPosition: LabelPosition
    
    // MARK: - Label Position
    enum LabelPosition {
        case leading
        case trailing
    }
    
    // MARK: - Initializer
    init(
        label: String? = nil,
        isOn: Binding<Bool>,
        enabled: Bool = true,
        labelPosition: LabelPosition = .leading
    ) {
        self.label = label
        self._isOn = isOn
        self.enabled = enabled
        self.labelPosition = labelPosition
    }
    
    // MARK: - Body
    var body: some View {
        HStack(spacing: AtomicSpacing.sm) {
            if labelPosition == .leading, let label = label {
                labelView(label)
                Spacer()
            }
            
            Toggle("", isOn: $isOn)
                .toggleStyle(AtomicToggleStyle())
                .disabled(!enabled)
                .opacity(enabled ? 1.0 : 0.6)
            
            if labelPosition == .trailing, let label = label {
                Spacer()
                labelView(label)
            }
        }
        .frame(minHeight: AtomicSpacing.minTouchTarget)
    }
    
    // MARK: - Helper Views
    private func labelView(_ text: String) -> some View {
        Text(text)
            .font(.system(size: 16))
            .foregroundColor(enabled ? .atomicOnSurface : .atomicOnSurfaceVariant)
    }
}

// MARK: - Atomic Toggle Style
struct AtomicToggleStyle: ToggleStyle {
    
    func makeBody(configuration: Configuration) -> some View {
        Button(action: {
            configuration.isOn.toggle()
        }) {
            HStack {
                // Track
                RoundedRectangle(cornerRadius: 16)
                    .fill(configuration.isOn ? Color.atomicPrimary : Color.atomicOnSurfaceVariant)
                    .frame(width: 50, height: 30)
                    .overlay(
                        // Thumb
                        Circle()
                            .fill(Color.white)
                            .frame(width: 26, height: 26)
                            .offset(x: configuration.isOn ? 10 : -10)
                            .animation(.easeInOut(duration: 0.2), value: configuration.isOn)
                    )
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}

// MARK: - Atomic Checkbox
// Checkbox atom component
// Matches Android AtomicCheckbox.kt implementation

struct AtomicCheckbox: View {
    
    // MARK: - Properties
    let label: String?
    @Binding var isChecked: Bool
    let enabled: Bool
    let labelPosition: LabelPosition
    
    // MARK: - Label Position
    enum LabelPosition {
        case leading
        case trailing
    }
    
    // MARK: - Initializer
    init(
        label: String? = nil,
        isChecked: Binding<Bool>,
        enabled: Bool = true,
        labelPosition: LabelPosition = .trailing
    ) {
        self.label = label
        self._isChecked = isChecked
        self.enabled = enabled
        self.labelPosition = labelPosition
    }
    
    // MARK: - Body
    var body: some View {
        HStack(spacing: AtomicSpacing.sm) {
            if labelPosition == .leading, let label = label {
                labelView(label)
                Spacer()
            }
            
            Button(action: {
                if enabled {
                    isChecked.toggle()
                }
            }) {
                ZStack {
                    // Background
                    RoundedRectangle(cornerRadius: AtomicShapes.xs)
                        .fill(isChecked ? Color.atomicPrimary : Color.clear)
                        .frame(width: 20, height: 20)
                        .overlay(
                            RoundedRectangle(cornerRadius: AtomicShapes.xs)
                                .stroke(
                                    isChecked ? Color.atomicPrimary : Color.atomicOnSurfaceVariant,
                                    lineWidth: 2
                                )
                        )
                    
                    // Checkmark
                    if isChecked {
                        Image(systemName: "checkmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.atomicOnPrimary)
                    }
                }
            }
            .buttonStyle(PlainButtonStyle())
            .disabled(!enabled)
            .opacity(enabled ? 1.0 : 0.6)
            .animation(.easeInOut(duration: 0.15), value: isChecked)
            
            if labelPosition == .trailing, let label = label {
                labelView(label)
                Spacer()
            }
        }
        .frame(minHeight: AtomicSpacing.minTouchTarget)
    }
    
    // MARK: - Helper Views
    private func labelView(_ text: String) -> some View {
        Text(text)
            .font(.system(size: 16))
            .foregroundColor(enabled ? .atomicOnSurface : .atomicOnSurfaceVariant)
    }
}

// MARK: - Preview
struct AtomicToggle_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: AtomicSpacing.lg) {
            AtomicToggle(
                label: "Enable notifications",
                isOn: .constant(true)
            )
            
            AtomicToggle(
                label: "Dark mode",
                isOn: .constant(false)
            )
            
            AtomicToggle(
                label: "Disabled toggle",
                isOn: .constant(true),
                enabled: false
            )
            
            Divider()
            
            AtomicCheckbox(
                label: "Accept terms and conditions",
                isChecked: .constant(true)
            )
            
            AtomicCheckbox(
                label: "Subscribe to newsletter",
                isChecked: .constant(false)
            )
            
            AtomicCheckbox(
                label: "Disabled checkbox",
                isChecked: .constant(true),
                enabled: false
            )
        }
        .atomicPadding()
        .previewLayout(.sizeThatFits)
    }
}