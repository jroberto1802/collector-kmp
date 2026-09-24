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

Abra `iosApp/iosApp.xcodeproj` no macOS com Xcode. O build phase do projeto executa `:composeApp:embedAndSignAppleFrameworkForXcode` automaticamente.

## Validação

```powershell
./gradlew.bat :composeApp:assembleDebug
./gradlew.bat :composeApp:testDebugUnitTest
```
