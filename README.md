# Collector KMP

POC multiplataforma do Collector para Android e iOS, recriada a partir do projeto React Native e do protótipo fornecido.

## Stack escolhida

- **Kotlin Multiplatform + Compose Multiplatform** para compartilhar UI, regras de apresentação e domínio.
- **MVVM com `StateFlow` e ViewModel KMP** para estado previsível e testável.
- **Ktor Client** para autenticação e sincronização REST.
- **Room KMP + SQLite bundled** para persistência offline compartilhada e consultas paginadas.
- **KVault** para Android Keystore e iOS Keychain, incluindo a opção de salvar a senha.
- **kotlinx.serialization** para os contratos das APIs.
- **Compottie** para reutilizar os arquivos Lottie JSON do popup de sincronização.

## Funcionalidades da POC

- Login integrado a `POST https://collector-func.azurewebsites.net/api/login`.
- Dados do login expostos separadamente em `AppConstants` e persistidos de forma segura.
- Sincronização obrigatória após o login.
- Token `client_credentials`, paginação completa de produtos e armazenamento local.
- Home fiel ao protótipo, menu lateral em formato de sheet e quatro opções.
- Confirmação de nova sincronização, com limpeza prévia da tabela de produtos.
- Listagem offline de produtos com busca, paginação e botão flutuante de sincronização.
- Logout com limpeza da sessão e retorno à tela de login.

## Estrutura

```text
composeApp/src/commonMain/
  kotlin/com/softcom/collector/
    core/          constantes e utilitários
    data/          API, Room, storage seguro e repositórios
    model/         modelos de domínio e DTOs
    presentation/  ViewModels e estados MVVM
    ui/            telas, componentes e tema compartilhados
  composeResources/
    drawable/      logos e ilustração
    files/         animações Lottie JSON
```

## Android

Requisitos: JDK 17+ e Android SDK 36.

```powershell
./gradlew.bat :composeApp:assembleDebug
```

APK gerado em `composeApp/build/outputs/apk/debug/composeApp-debug.apk`.

## iOS

Requisitos: **macOS**, Xcode e **JDK 17** (Temurin/Zulu). Evite Java 22+.

### Importante

`embedAndSignAppleFrameworkForXcode` é tarefa de **integração do Xcode**.  
Rodar só `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` no Terminal **sem** as variáveis do Xcode costuma falhar (mensagem estranha tipo `What went wrong: 27`).

### 1) Preparar o ambiente (Terminal)

```bash
cd /caminho/para/collector-kmp

# Use JDK 17 (nao Java 24)
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

chmod +x ./gradlew

# Teste de compilacao iOS no Terminal (este SIM funciona fora do Xcode):
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 --stacktrace
```

Se esse comando falhar, o log `--stacktrace` mostra a causa (SDK Android ausente, etc.).

Android SDK no Mac (mesmo para build iOS, o módulo usa plugin Android):
- Instale Android Studio **ou** Command-line Tools
- Crie `local.properties` na raiz:

```properties
sdk.dir=/Users/SEU_USER/Library/Android/sdk
```

### 2) Rodar no Xcode

```bash
open iosApp/iosApp.xcodeproj
```

1. Target **Collector** → **Signing & Capabilities** → escolha seu **Team**
2. Simulador (iPhone) → ▶ Run

O script do Xcode já define `JAVA_HOME` e chama o Gradle com `--no-configuration-cache`.

### 3) Se ainda falhar no Xcode

1. Report navigator → último build → abra o log do phase **Build ComposeApp framework**
2. Product → Clean Build Folder
3. No Terminal, confirme o framework:

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Validação

```powershell
./gradlew.bat :composeApp:assembleDebug
./gradlew.bat :composeApp:testDebugUnitTest
```
