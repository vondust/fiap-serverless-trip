## Aplicação AWS SAM para Gerenciamento de Fotos

## Requerimentos

* AWS CLI já instalado e configurado com permissão mínima de PowerUser
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)
* [SAM CLI](https://github.com/awslabs/aws-sam-cli)
* [Python 3](https://docs.python.org/3/)

## Setup

### Instalando dependências

Use o `maven` para instalar as dependências e pacotes da aplicação, compilando tudo em um único arquivo Jar:

```bash
mvn install
```

### Desenvolvimento Local

**Network Docker**

Crie uma network docker através do seguinte comando:  `docker network create -d bridge lambda-local`

**Invocando funções localmente através do API Gateway**
1. Inicie o DynamoDB Local em um Container Docker. `docker run -d -p 8000:8000 --network lambda-local --name dynamodb-local amazon/dynamodb-local`

2. Crie a tabela trip no DynamoDB. `aws dynamodb create-table --table-name trip --attribute-definitions AttributeName=id,AttributeType=S AttributeName=dateTrip,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --global-secondary-indexes 'IndexName=dateTrip-index,KeySchema=[{AttributeName=id,KeyType=HASH},{AttributeName=dateTrip,KeyType=RANGE}],Projection={ProjectionType=ALL}' --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000`

Se a tabela já existe, é desejável que seja a tabela seja excluída primeiramente: `aws dynamodb delete-table --table-name trip --endpoint-url http://localhost:8000`

3. Inicie o local API do SAM.
 - No Mac: `sam local start-api --env-vars src/test/resources/test_environment_mac.json --docker-network lambda-local`
 - No Windows: `sam local start-api --env-vars src/test/resources/test_environment_windows.json --docker-network lambda-local`
 - No Linux: `sam local start-api --env-vars src/test/resources/test_environment_linux.json --docker-network lambda-local`

Se o comando anterior foi executado com sucesso, será então possível acessar o seguinte endpoint para realizar ações:  `http://localhost:3000/trip/period?start=2020-01-02&end=2020-02-02`.

Utilize a coleção do Postman src/test/resources/34scj-serverless_trip.postman_collection.json, para explorar as outras funções.

**SAM CLI** é usado para emular o AWS Lambda e o AWS API Gateway localmente, usando o arquivo `template.yaml`.

## Packaging and deployment

O AWS Lambda para runtime Java aceita tanto um arquivo zip quanto um arquivo JAR. O SAM usa a propriedade `CodeUri` para saber onde localizar a aplicação e suas dependências.

Primeiro, precisamos de um `bucket S3` na qual podemos fazer o upload das funções Lambda empacotadas no ZIP, antes de implantar qualquer coisa.

```bash
export BUCKET_NAME=my_new_bucket_name
aws s3 mb s3://$BUCKET_NAME
```

Agora, execute o seguinte comando para 'empacotar' nossas funções Lambda para o S3:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket $BUCKET_NAME
```

O próximo comando cria um Stack Cloudformation e implanta os nossos recursos SAM:

```bash
sam deploy \
    --template-file packaged.yaml \
    --stack-name study-datalake \
    --capabilities CAPABILITY_IAM
```

Após completar a implantação, podemos executar o seguinte comando, a fim de buscar a URL do API Gateway criado na AWS:

```bash
aws cloudformation describe-stacks \
    --stack-name sam-orderHandler \
    --query 'Stacks[].Outputs'
```
