#!/bin/bash
set -euo pipefail

REGION="${AWS_REGION:-us-east-1}"
ENDPOINT_URL="${LOCALSTACK_ENDPOINT_URL:-http://localhost:4566}"

if command -v awslocal >/dev/null 2>&1; then
    DDB_CMD=(awslocal dynamodb)
elif command -v aws >/dev/null 2>&1; then
    DDB_CMD=(aws --endpoint-url "$ENDPOINT_URL" dynamodb)
else
    echo "Error: neither 'awslocal' nor 'aws' CLI was found in PATH." >&2
    exit 1
fi

echo "Starting DynamoDB table creation..."

if "${DDB_CMD[@]}" describe-table --table-name url --region "$REGION" >/dev/null 2>&1; then
    echo "DynamoDB table 'url' already exists. Skipping creation."
    exit 0
fi

"${DDB_CMD[@]}" create-table \
    --table-name url \
    --attribute-definitions AttributeName=shortCode,AttributeType=S \
    --key-schema AttributeName=shortCode,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region "$REGION"

echo "DynamoDB table 'url' created successfully."
echo "Store item attributes: shortcode (S), long_url (S), created_at (S in ISO-8601 or N in epoch)."
