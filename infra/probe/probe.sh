#!/bin/sh
# One-shot DHCP probe: performs a real, handshake-only DHCP exchange against
# dnsmasq, announcing the hostname "probe", then exits.

set -eu

interface=eth0

# An exited container reports no MAC through docker inspect, so say it here.
# verify.sh reads the MAC from this exact line.
echo "probe: MAC address $(cat "/sys/class/net/$interface/address")"

# -s /bin/true: run no configuration script. The default script would flush
# this container's own Docker address and replace it with the leased one.
# -t 5 -T 2: five discovers, two seconds apart.
exec udhcpc -i "$interface" -f -q -n -t 5 -T 2 -s /bin/true -x "hostname:probe"
