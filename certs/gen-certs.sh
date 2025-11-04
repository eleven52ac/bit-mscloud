
PASSWORD="changeit"
CA_NAME="BitMSCloudCA"
SERVER_CN="192.168.31.57"
CLIENT_CN="gateway"

echo "🧹 清理旧文件..."
rm -f *.pem *.p12 *.csr *.srl *.cnf

# === 1️⃣ 生成 CA 根证书 ===
echo "📜 生成 CA 根证书..."
openssl genrsa -out ca-key.pem 4096
openssl req -x509 -new -nodes \
  -key ca-key.pem \
  -sha256 -days 3650 \
  -out ca-cert.pem \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=BitMS/OU=CA/CN=$CA_NAME"

# === 2️⃣ 生成服务端证书 (Elasticsearch 等) ===
echo "💡 生成服务端证书..."
openssl genrsa -out server-key.pem 2048
openssl req -new -key server-key.pem -out server.csr \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=BitMS/OU=Server/CN=$SERVER_CN"

cat > server-ext.cnf <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
IP.1 = 192.168.31.57
IP.2 = 100.120.86.63
IP.3 = 100.97.223.54
DNS.1 = localhost
EOF

openssl x509 -req -in server.csr \
  -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial \
  -out server-cert.pem -days 1095 -sha256 -extfile server-ext.cnf

# === 3️⃣ 生成客户端 (Gateway) 证书 ===
echo "🛰️ 生成客户端 (Gateway) 证书..."
openssl genrsa -out gateway-key.pem 2048
openssl req -new -key gateway-key.pem -out gateway.csr \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=BitMS/OU=Gateway/CN=$CLIENT_CN"

cat > gateway-ext.cnf <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = clientAuth
EOF

openssl x509 -req -in gateway.csr \
  -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial \
  -out gateway-cert.pem -days 1095 -sha256 -extfile gateway-ext.cnf

# === 4️⃣ 生成 keystore / truststore ===
echo "🔐 生成 keystore / truststore..."

# 服务端 keystore
openssl pkcs12 -export \
  -in server-cert.pem \
  -inkey server-key.pem \
  -out server-keystore.p12 \
  -name "server" \
  -CAfile ca-cert.pem \
  -caname "$CA_NAME" \
  -password pass:$PASSWORD

# 客户端 keystore
openssl pkcs12 -export \
  -in gateway-cert.pem \
  -inkey gateway-key.pem \
  -out gateway-keystore.p12 \
  -name "gateway" \
  -CAfile ca-cert.pem \
  -caname "$CA_NAME" \
  -password pass:$PASSWORD

# Truststore (双方通用)
keytool -importcert -trustcacerts \
  -alias "$CA_NAME" \
  -file ca-cert.pem \
  -keystore ca-truststore.p12 \
  -storetype PKCS12 \
  -storepass $PASSWORD \
  -noprompt

echo "✅ 所有证书生成完成!"
ls -lh *.pem *.p12

# 验证
echo ""
echo "🔍 验证 CA 链..."
openssl verify -CAfile ca-cert.pem server-cert.pem
openssl verify -CAfile ca-cert.pem gateway-cert.pem
