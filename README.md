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

Requisitos: **macOS**, Xcode e **JDK 17+** (Temurin/Zulu).

### 1) Preparar o ambiente (Terminal)

```bash
cd /caminho/para/collector-kmp

# Confirme o Java 17+
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Gradlew precisa ser executável (após clonar do Windows)
chmod +x ./gradlew

# Teste o framework fora do Xcode (mostra o erro real, se houver)
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

### 2) Abrir e rodar no Xcode

```bash
open iosApp/iosApp.xcodeproj
```

Selecione um simulador (ex.: iPhone 16) e rode ▶.

Se ainda falhar em **Build ComposeApp framework**:
1. No Xcode: **Report navigator** (ícone de balão) → último build → abra o log vermelho do script.
2. No target **Collector** → **Signing & Capabilities** → escolha seu **Team** (Apple ID).
3. Product → Clean Build Folder, depois rode de novo.

O build phase já tenta definir `JAVA_HOME` automaticamente; o passo 1 acima confirma se o Gradle consegue compilar.

## Validação

```powershell
./gradlew.bat :composeApp:assembleDebug
./gradlew.bat :composeApp:testDebugUnitTest
```
