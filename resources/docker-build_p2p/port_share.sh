#!/bin/bash -e

function usage {
    echo "
Usage: $0 [OPTIONS] <local_port> <stunDumpFullPath>

Share a local port through a ssh tunnel.
The options from command line override the settings
on the config file 'ssh.rc'.

    --help                 display this help screen
    --p2p_server=<server>  IP or DN of the p2p server
    --p2p_port=<port>      port of the sshd service in the p2p server (2201)
    --compress=yes         compress the ssh connections
"
    exit 0
}

### go to the script directory
cd $(dirname $0)

### get the config
. ./ssh.rc

### get the options from command line
for opt in "$@"
do
    case $opt in
	-h|--help)     usage ;;
	--p2p_server=*)  p2p_server=${opt#*=} ;;
	--p2p_port=*)    p2p_port=${opt#*=} ;;
	--compress=*)    compress=${opt#*=} ;;
	*)
            if [ ${opt:0:1} = '-' ]; then usage; fi

            ;;

    esac
done

if [ "$1" = '' ] || [ "$2" = '' ]
then
    usage
fi

local_port="$1";
stunDumpFullPath="$2";

### check the presence of the required arguments
if [ "$local_port" = '' ]
then
    echo -e "\nError: Required argument <local_port> is missing."
    usage
fi

### create a key pair for the connection and get
### the key name, remote port and the private key
keyfile=$(tempfile)
ssh -o StrictHostKeyChecking=no -p $p2p_port -i keys/create.key vnc@$p2p_server > $keyfile 2>>$logfile
key=$(sed -n -e '1p' $keyfile | tr -d [:space:])
remote_port=$(sed -n -e '2p' $keyfile | tr -d [:space:])

###exctract upload key
uploadkey=$(tempfile)
sed -n '30,$w '$uploadkey $keyfile
sed -i '30,$d' $keyfile
chmod 0600 $keyfile
chmod 0600 $uploadkey

dumpUploadfile=$(tempfile -n /tmp/$key.stn)
cp $stunDumpFullPath $dumpUploadfile

###upload file
scp -q -o StrictHostKeyChecking=no -P $p2p_port -i $uploadkey $dumpUploadfile vnc@$p2p_server:~/stun_dumps >$logfile 2>>$logfile

### start the tunnel for port-forwarding
ssh="ssh -o StrictHostKeyChecking=no -p $p2p_port -f -N -t"
if [ $compress = 'yes' ]; then ssh="$ssh -C"; fi
$ssh -R $remote_port:localhost:$local_port \
-i $keyfile vnc@$p2p_server 2>>$logfile

rm $dumpUploadfile
rm $keyfile
rm $uploadkey

### output the key name
echo $key