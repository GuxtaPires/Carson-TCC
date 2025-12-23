# Carson - Aplicativo para Descarte de Medicamentos

Este repositório contém um aplicativo Android (Java) que ajuda usuários a localizar pontos de descarte de medicamentos, gerenciar lista de medicamentos e acessar conteúdo educativo sobre descarte consciente.

Resumo das principais funcionalidades

- Tela inicial com botão "Começar" e fluxo de login simples.
- Tela de login (`tela_de_login`) com validação básica e link para cadastro.
- Cadastro de usuário (`Cadastro_dados_pessoais`) com validações simples e retorno ao login.
- Tela de lista de medicamentos (`MedicamentosActivity`) com barra lateral (Drawer), busca e botão de adicionar.
- Tela de mapa (`MapActivity`) que exibe Google Maps, permite selecionar um ponto (marker) e converte coordenadas em endereço via Geocoder.
- Tela de notícias/atualizações (`HomeActivity`) com bottom navigation e drawer.
- Tela de notificações (`NotificacoesActivity`) que gera notificações de exemplo dinamicamente.
- Tela de edição de perfil (`ProfileActivity`) com validação de campos.
- Dashboard do moderador (`activity_moderador_dashboard`) com BottomSheet para adicionar pontos de descarte.

Arquivos/chaves importantes

- `app/build.gradle.kts` - dependências do módulo (Firebase adicionadas) e aplicação do plugin Google Services.
- `app/google-services.json` - já presente no projeto (contém configuração do Firebase para o app). Não compartilhe esse arquivo publicamente.
- `app/src/main/AndroidManifest.xml` - declara atividades e a `meta-data` com a chave da API do Google Maps.
- `app/src/main/java/com/example/...` - pacote com as Activities em Java.
- `app/src/main/res/layout/` - layouts XML usados pelas Activities.

Dependências relevantes adicionadas

No `app/build.gradle.kts` foram incluídas (via BOM do Firebase):
- com.google.firebase:firebase-bom:31.2.3
- com.google.firebase:firebase-analytics
- com.google.firebase:firebase-auth
- com.google.firebase:firebase-database

Também foi aplicada a plugin `com.google.gms.google-services` no módulo.

Permissões e APIs

- Permissões de localização: ACCESS_FINE_LOCATION e ACCESS_COARSE_LOCATION (declared em `AndroidManifest.xml`).
- Google Maps API key está presente em `AndroidManifest.xml` como `com.google.android.geo.API_KEY`. Verifique se a chave é sua e que está habilitada no Google Cloud Console.

Instruções para build e testes

1. Abra o projeto no Android Studio.
2. Sincronize o Gradle (File -> Sync Project with Gradle Files).
3. Verifique se `app/google-services.json` corresponde ao pacote `applicationId` (com.example.carson_umaplicativoparadescartedemedicamentos) no `build.gradle.kts`.
4. Conecte um dispositivo ou use um emulador com Google Play services.
5. Rode `Run` ou use Gradle na linha de comando:

```bash
# No Windows (cmd.exe) a partir da raiz do projeto
gradlew assembleDebug
```

Notas de segurança

- Não comite chaves sensíveis (API keys, google-services.json) em repositórios públicos.
- Revise a chave do Maps e limite seu uso por pacote e SHA-1 no Google Cloud Console.

Problemas conhecidos e próximos passos

- O `classpath("com.google.gms:google-services:4.4.2")` foi mantido no `app/build.gradle.kts` e funciona, mas o local mais apropriado é o `build.gradle(.kts)` do projeto raiz. Recomendo mover essa dependência para lá.
- Algumas Activities usam pacotes diferentes nas declarações (por exemplo, `package com.seuprojeto.carson;` em alguns arquivos). Isso pode causar conflitos. É recomendável alinhar todas as classes para o mesmo package: `com.example.carson_umaplicativoparadescartedemedicamentos`.
- Se você mencionou erro em Preference XML: envie o arquivo XML específico (`res/xml/preferences.xml` ou similar) que está dando erro que eu corrijo.

Sugestões de melhoria

- Integrar autenticação real com Firebase Auth.
- Salvar pontos de descarte e medicamentos no Firebase Realtime Database ou Firestore.
- Implementar busca real e filtros em `MedicamentosActivity`.
- Mover o `google-services` classpath para o root e atualizar para uma versão mais recente do plugin.

Se quiser que eu:
- Mova o classpath do plugin do Google Services para o `build.gradle.kts` do projeto raiz (faço isso aqui); e/ou
- Corrija o package inconsistente nas classes (ajusto automaticamente); e/ou
- Corrija o XML de Preference que você mencionou (envie o arquivo XML ou seu caminho),
então diga qual dessas ações prefere que eu execute e eu faço as alterações e executo uma build para validar.

