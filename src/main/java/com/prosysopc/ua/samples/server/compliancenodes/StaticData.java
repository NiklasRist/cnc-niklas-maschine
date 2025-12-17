/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import java.util.EnumSet;
import java.util.UUID;

import com.prosysopc.ua.stack.builtintypes.ByteString;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.UnsignedByte;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.builtintypes.UnsignedLong;
import com.prosysopc.ua.stack.builtintypes.UnsignedShort;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.builtintypes.XmlElement;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.utils.CryptoUtil;

/**
 * This enum will be used to construct StaticData folder.
 *
 */
public enum StaticData implements CommonComplianceInfo {
  VARIANT("Variant", Identifiers.BaseDataType, Boolean.TRUE),

  BOOLEAN("Boolean", Identifiers.Boolean, Boolean.TRUE),

  BYTE("Byte", Identifiers.Byte, UnsignedByte.valueOf(0)),

  BYTE_STRING("ByteString", Identifiers.ByteString, ByteString.valueOf((byte) 0)),

  DATE_TIME("DateTime", Identifiers.DateTime, DateTime.currentTime()),

  DOUBLE("Double", Identifiers.Double, Double.valueOf(0)),

  FLOAT("Float", Identifiers.Float, Float.valueOf(0)),

  GUID("GUID", Identifiers.Guid, UUID.randomUUID()),

  INT16("Int16", Identifiers.Int16, Short.valueOf((short) 0)),

  INT32("Int32", Identifiers.Int32, Integer.valueOf(0)),

  INT64("Int64", Identifiers.Int64, Long.valueOf(0)),

  SBYTE("SByte", Identifiers.SByte, Byte.valueOf((byte) 0)),

  STRING("String", Identifiers.String, "TestString"),

  UINT16("UInt16", Identifiers.UInt16, UnsignedShort.valueOf(0)),

  UINT32("UInt32", Identifiers.UInt32, UnsignedInteger.valueOf(0)),

  UINT64("UInt64", Identifiers.UInt64, UnsignedLong.valueOf(0)),

  XML_ELEMENT("XmlElement", Identifiers.XmlElement, new XmlElement("<testElement />")),

  // some additional nodes needed..
  DURATION("Duration", Identifiers.Duration, Double.valueOf(0.0)),

  QUALIFIED_NAME("QualifiedName", Identifiers.QualifiedName, QualifiedName.DEFAULT_BINARY_ENCODING),

  LOCALIZED_TEXT("LocalizedText", Identifiers.LocalizedText, LocalizedText.english("Test Text")),

  NODE_ID("NodeId", Identifiers.NodeId, Identifiers.NodeId),

  // even more additional nodes needed
  IMAGE("Image", Identifiers.Image, ByteString.valueOf(CryptoUtil.hexToBytes(
      "0xffd8ffdb004300080606070605080707070909080a0c140d0c0b0b0c1912130f141d1a1f1e1d1a1c1c20242e2720222c231c1c2837292c30313434341f27393d38323c2e333432ffdb0043010909090c0b0c180d0d1832211c213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232ffc00014080064006404012200021101031101042200ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000e040100021103110400003f00f54a28a283e24f7fa28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a2bffd9"))),

  IMAGE_JPG("ImageJPG", Identifiers.ImageJPG, ByteString.valueOf(CryptoUtil.hexToBytes(
      "0xffd8ffdb004300080606070605080707070909080a0c140d0c0b0b0c1912130f141d1a1f1e1d1a1c1c20242e2720222c231c1c2837292c30313434341f27393d38323c2e333432ffdb0043010909090c0b0c180d0d1832211c213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232ffc00014080064006404012200021101031101042200ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000e040100021103110400003f00f54a28a283e24f7fa28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a28a28a2800a28a2bffd9"))),

  IMAGE_BMP("ImageBMP", Identifiers.ImageBMP, ByteString.valueOf(CryptoUtil.hexToBytes(
      "0x424de61d000000000000360000002800000032000000320000000100180000000000b01d000000000000000000000000000000000000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000d4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7fd4ff7f0000"))),

  IMAGE_PNG("ImagePNG", Identifiers.ImagePNG, ByteString.valueOf(CryptoUtil.hexToBytes(
      "0x89504e470d0a1a0a0000000d494844520000006400000064080600000070e29554000000a64944415478daedd1410d000008c4309c63123f870d42fa9881b53a13dda94c002220400404888000111020460011102002024440800808100111102002024440800808100111102002024440800808100111102002024440800808100111102002024440800808100111102002024440800808100111102002024440800808100111102002024440800808100111102002024440800808100101620210010122204004e47b0bf77893a025f17da70000000049454e44ae426082"))),

  IMAGE_GIF("ImageGIF", Identifiers.ImageGIF, ByteString.valueOf(CryptoUtil.hexToBytes(
      "0x47494638396164006400f000007fffd47fffd42c00000000640064004008a10001081c48b0a0c18308132a5cc8b0a1c38710234a9c48b1a2c58b18336adcc8b1a3c78f20438a1c49b2a4c9932853aa5cc9b2a5cb973063ca9c49b3a6cd9b3873eadcc9b3a7cf9f40830a1d4ab4a8d1a348932a5dcab4a9d3a750a34a9d4ab5aad5ab58b36addcab5abd7af60c38a1d4bb6acd9b368d3aa5dcbb6addbb770e3ca9d4bb7aeddbb78f3eaddcbb7afdfbf80030b1e4cb8b0e1c388132b5ecc587040003b"))),

  INTEGER("Integer", Identifiers.Integer, 1),

  LOCALE_ID("LocaleId", Identifiers.LocaleId, "en"),

  NUMBER("Number", Identifiers.Number, 1),

  UINTEGER("UInteger", Identifiers.UInteger, UnsignedInteger.valueOf(0)),

  UTC_TIME("UtcTime", Identifiers.UtcTime, DateTime.currentTime()),

  ENUMERATION("Enumeration", Identifiers.Enumeration, null),

  // "Array" postfix should be added when created..
  VARIANT_ARRAY("Variant", Identifiers.BaseDataType,
      new Variant[] {new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE),
          new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE)}),

  VARIANT_MULTIDIMENSIONAL_ARRAY("Variant", Identifiers.BaseDataType,
      new Variant[][] {
          {new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE),
              new Variant(Boolean.TRUE)},
          {new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE),
              new Variant(Boolean.TRUE)},
          {new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE),
              new Variant(Boolean.TRUE)},
          {new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE),
              new Variant(Boolean.TRUE)},
          {new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE), new Variant(Boolean.TRUE),
              new Variant(Boolean.TRUE)}}),

  LOCALIZED_TEXT_ARRAY("LocalizedText", Identifiers.LocalizedText,
      new LocalizedText[] {LocalizedText.english("Text1"), LocalizedText.english("Text2"),
          LocalizedText.english("Text3"), LocalizedText.english("Text4"), LocalizedText.english("Text5"),
          LocalizedText.english("Text6")}),

  LOCALIZED_TEXT_MULTIDIMENSIONAL_ARRAY("LocalizedText", Identifiers.LocalizedText,
      new LocalizedText[][] {
          {LocalizedText.english("Text1"), LocalizedText.english("Text2"), LocalizedText.english("Text3"),
              LocalizedText.english("Text4"), LocalizedText.english("Text5")},
          {LocalizedText.english("Text6"), LocalizedText.english("Text7"), LocalizedText.english("Text8"),
              LocalizedText.english("Text9"), LocalizedText.english("Text10")},
          {LocalizedText.english("Text11"), LocalizedText.english("Text12"), LocalizedText.english("Text13"),
              LocalizedText.english("Text14"), LocalizedText.english("Text15")},
          {LocalizedText.english("Text16"), LocalizedText.english("Text17"), LocalizedText.english("Text18"),
              LocalizedText.english("Text19"), LocalizedText.english("Text20")},
          {LocalizedText.english("Text21"), LocalizedText.english("Text22"), LocalizedText.english("Text23"),
              LocalizedText.english("Text24"), LocalizedText.english("Text25")}}),

  QUALIFIED_NAME_ARRAY("QualifiedName", Identifiers.QualifiedName,
      new QualifiedName[] {QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
          QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
          QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING}),

  QUALIZIED_NAME_MULTIDIMENSIONAL_ARRAY("QualifiedName", Identifiers.QualifiedName,
      new QualifiedName[][] {
          {QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING},
          {QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING},
          {QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING},
          {QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING},
          {QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING,
              QualifiedName.DEFAULT_BINARY_ENCODING, QualifiedName.DEFAULT_XML_ENCODING}}),

  BOOLEAN_ARRAY("Boolean", Identifiers.Boolean, new Boolean[] {true, false, true, false, false}),

  BOOLEAN_MULTIDIMENSIONAL_ARRAY("Boolean", Identifiers.Boolean,
      new Boolean[][] {{true, true, true, true, true}, {true, true, true, true, true}, {true, true, true, true, true},
          {true, true, true, true, true}, {true, true, true, true, true}}),

  BYTE_ARRAY("Byte", Identifiers.Byte, new UnsignedByte[] {UnsignedByte.valueOf(1), UnsignedByte.valueOf(2),
      UnsignedByte.valueOf(3), UnsignedByte.valueOf(4), UnsignedByte.valueOf(5)}),

  BYTE_MULTIDIMENSIONAL_ARRAY("Byte", Identifiers.Byte,
      new UnsignedByte[][] {
          {UnsignedByte.valueOf(1), UnsignedByte.valueOf(2), UnsignedByte.valueOf(3), UnsignedByte.valueOf(4),
              UnsignedByte.valueOf(5)},
          {UnsignedByte.valueOf(6), UnsignedByte.valueOf(7), UnsignedByte.valueOf(8), UnsignedByte.valueOf(9),
              UnsignedByte.valueOf(10)},
          {UnsignedByte.valueOf(11), UnsignedByte.valueOf(12), UnsignedByte.valueOf(13), UnsignedByte.valueOf(14),
              UnsignedByte.valueOf(15)},
          {UnsignedByte.valueOf(16), UnsignedByte.valueOf(17), UnsignedByte.valueOf(18), UnsignedByte.valueOf(19),
              UnsignedByte.valueOf(20)},
          {UnsignedByte.valueOf(21), UnsignedByte.valueOf(22), UnsignedByte.valueOf(23), UnsignedByte.valueOf(24),
              UnsignedByte.valueOf(25)}}),

  BYTE_STRING_ARRAY("ByteString", Identifiers.ByteString,
      new ByteString[] {ByteString.valueOf(), ByteString.valueOf((byte) 1, (byte) 2, (byte) 3, (byte) 4),
          ByteString.valueOf((byte) 2, (byte) 3, (byte) 4, (byte) 5),
          ByteString.valueOf((byte) 3, (byte) 4, (byte) 5, (byte) 6),
          ByteString.valueOf((byte) 4, (byte) 5, (byte) 6, (byte) 7),
          ByteString.valueOf((byte) 5, (byte) 6, (byte) 7, (byte) 8)}),

  BYTE_STRING_MULTIDIMENSIONAL_ARRAY("ByteString", Identifiers.ByteString,
      new ByteString[][] {
          {ByteString.valueOf(), ByteString.valueOf((byte) 1, (byte) 2, (byte) 3, (byte) 4),
              ByteString.valueOf((byte) 2, (byte) 3, (byte) 4, (byte) 5),
              ByteString.valueOf((byte) 3, (byte) 4, (byte) 5, (byte) 6),
              ByteString.valueOf((byte) 4, (byte) 5, (byte) 6, (byte) 7),
              ByteString.valueOf((byte) 5, (byte) 6, (byte) 7, (byte) 8)},
          {ByteString.valueOf(), ByteString.valueOf((byte) 1, (byte) 2, (byte) 3, (byte) 4),
              ByteString.valueOf((byte) 2, (byte) 3, (byte) 4, (byte) 5),
              ByteString.valueOf((byte) 3, (byte) 4, (byte) 5, (byte) 6),
              ByteString.valueOf((byte) 4, (byte) 5, (byte) 6, (byte) 7),
              ByteString.valueOf((byte) 5, (byte) 6, (byte) 7, (byte) 8)},
          {ByteString.valueOf(), ByteString.valueOf((byte) 1, (byte) 2, (byte) 3, (byte) 4),
              ByteString.valueOf((byte) 2, (byte) 3, (byte) 4, (byte) 5),
              ByteString.valueOf((byte) 3, (byte) 4, (byte) 5, (byte) 6),
              ByteString.valueOf((byte) 4, (byte) 5, (byte) 6, (byte) 7),
              ByteString.valueOf((byte) 5, (byte) 6, (byte) 7, (byte) 8)},
          {ByteString.valueOf(), ByteString.valueOf((byte) 1, (byte) 2, (byte) 3, (byte) 4),
              ByteString.valueOf((byte) 2, (byte) 3, (byte) 4, (byte) 5),
              ByteString.valueOf((byte) 3, (byte) 4, (byte) 5, (byte) 6),
              ByteString.valueOf((byte) 4, (byte) 5, (byte) 6, (byte) 7),
              ByteString.valueOf((byte) 5, (byte) 6, (byte) 7, (byte) 8)},
          {ByteString.valueOf(), ByteString.valueOf((byte) 1, (byte) 2, (byte) 3, (byte) 4),
              ByteString.valueOf((byte) 2, (byte) 3, (byte) 4, (byte) 5),
              ByteString.valueOf((byte) 3, (byte) 4, (byte) 5, (byte) 6),
              ByteString.valueOf((byte) 4, (byte) 5, (byte) 6, (byte) 7),
              ByteString.valueOf((byte) 5, (byte) 6, (byte) 7, (byte) 8)}}),

  DATA_TIME_ARRAY("DateTime", Identifiers.DateTime, new DateTime[] {DateTime.currentTime(), DateTime.currentTime(),
      DateTime.currentTime(), DateTime.currentTime(), DateTime.currentTime()}),

  DATE_TIME_MULTIDIMENSIONAL_ARRAY("DateTime", Identifiers.DateTime,
      new DateTime[][] {
          {DateTime.currentTime(), DateTime.currentTime(), DateTime.currentTime(), DateTime.currentTime(),
              DateTime.currentTime()},
          {DateTime.currentTime(), DateTime.currentTime(), DateTime.currentTime(), DateTime.currentTime(),
              DateTime.currentTime()}}),

  DOUBLE_ARRAY("Double", Identifiers.Double, new Double[] {(double) 1, (double) 2, (double) 3, (double) 4, (double) 5}),

  DOUBLE_MULTIDIMENSIONAL_ARRAY("Double", Identifiers.Double,
      new Double[][] {{(double) 1, (double) 2, (double) 3, (double) 4, (double) 5},
          {(double) 6, (double) 7, (double) 8, (double) 9, (double) 10},
          {(double) 11, (double) 12, (double) 13, (double) 14, (double) 15},
          {(double) 16, (double) 17, (double) 18, (double) 19, (double) 20},
          {(double) 21, (double) 22, (double) 23, (double) 24, (double) 25}}),

  DURATION_ARRAY("Duration", Identifiers.Duration,
      new Double[] {(double) 1, (double) 2, (double) 3, (double) 4, (double) 5}),

  DURATION_MULTIDIMENSIONAL_ARRAY("Duration", Identifiers.Duration,
      new Double[][] {{(double) 1, (double) 2, (double) 3, (double) 4, (double) 5},
          {(double) 6, (double) 7, (double) 8, (double) 9, (double) 10},
          {(double) 11, (double) 12, (double) 13, (double) 14, (double) 15},
          {(double) 16, (double) 17, (double) 18, (double) 19, (double) 20},
          {(double) 21, (double) 22, (double) 23, (double) 24, (double) 25}}),

  FLOAT_ARRAY("Float", Identifiers.Float, new Float[] {(float) 1, (float) 2, (float) 3, (float) 4, (float) 5}),

  FLOAT_MULTIDIMENSIONAL_ARRAY("Float", Identifiers.Float,
      new Float[][] {{(float) 1, (float) 2, (float) 3, (float) 4, (float) 5},
          {(float) 6, (float) 7, (float) 8, (float) 9, (float) 10},
          {(float) 11, (float) 12, (float) 13, (float) 14, (float) 15},
          {(float) 16, (float) 17, (float) 18, (float) 19, (float) 20},
          {(float) 21, (float) 22, (float) 23, (float) 24, (float) 25}}),

  GUID_ARRAY("GUIDArray", Identifiers.Guid,
      new UUID[] {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()}),

  GUID_MULTIDIMENSIONAL_ARRAY("GUIDArray", Identifiers.Guid,
      new UUID[][] {{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()},
          {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()},
          {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()},
          {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()},
          {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()}}),

  INT16_ARRAY("Int16", Identifiers.Int16, new Short[] {(short) 1, (short) 2, (short) 3, (short) 4, (short) 5}),

  INT16_MULTIDIMENSIONAL_ARRAY("Int16", Identifiers.Int16,
      new Short[][] {{(short) 1, (short) 2, (short) 3, (short) 4, (short) 5},
          {(short) 6, (short) 7, (short) 8, (short) 9, (short) 10},
          {(short) 11, (short) 12, (short) 13, (short) 14, (short) 15},
          {(short) 16, (short) 17, (short) 18, (short) 19, (short) 20},
          {(short) 21, (short) 22, (short) 23, (short) 24, (short) 25}}),

  INT32_ARRAY("Int32", Identifiers.Int32, new Integer[] {1, 2, 3, 4, 5}),

  INT32_MULTIDIMENSIONAL_ARRAY("Int32", Identifiers.Int32, new Integer[][] {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10},
      {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}}),

  INT64_ARRAY("Int64", Identifiers.Int64, new Long[] {(long) 1, (long) 2, (long) 3, (long) 4, (long) 5}),

  INT64_MULTIDIMENSIONAL_ARRAY("Int64", Identifiers.Int64,
      new Long[][] {{(long) 1, (long) 2, (long) 3, (long) 4, (long) 5},
          {(long) 6, (long) 7, (long) 8, (long) 9, (long) 10}, {(long) 11, (long) 12, (long) 13, (long) 14, (long) 15},
          {(long) 16, (long) 17, (long) 18, (long) 19, (long) 20},
          {(long) 21, (long) 22, (long) 23, (long) 24, (long) 25}}),

  SBYTE_ARRAY("SByte", Identifiers.SByte, new Byte[] {(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5}),

  SBYTE_MULTIDIMENSIONAL_ARRAY("SByte", Identifiers.SByte,
      new Byte[][] {{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5},
          {(byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10}, {(byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15},
          {(byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20},
          {(byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25}}),

  STRING_ARRAY("String", Identifiers.String,
      new String[] {"testString1", "testString2", "testString3", "testString4", "testString5"}),

  STRING_MULTIDIMENSIONAL_ARRAY("String", Identifiers.String,
      new String[][] {{"testString1", "testString2", "testString3", "testString4", "testString5"},
          {"testString6", "testString7", "testString8", "testString9", "testString10"},
          {"testString11", "testString12", "testString13", "testString14", "testString15"},
          {"testString16", "testString17", "testString18", "testString19", "testString20"},
          {"testString21", "testString22", "testString23", "testString24", "testString25"}}),

  UINT16_ARRAY("UInt16", Identifiers.UInt16, new UnsignedShort[] {UnsignedShort.valueOf(1), UnsignedShort.valueOf(2),
      UnsignedShort.valueOf(3), UnsignedShort.valueOf(4), UnsignedShort.valueOf(5)}),

  UINT16_MULTIDIMENSIONAL_ARRAY("UInt16", Identifiers.UInt16,
      new UnsignedShort[][] {
          {UnsignedShort.valueOf(1), UnsignedShort.valueOf(2), UnsignedShort.valueOf(3), UnsignedShort.valueOf(4),
              UnsignedShort.valueOf(5)},
          {UnsignedShort.valueOf(6), UnsignedShort.valueOf(7), UnsignedShort.valueOf(8), UnsignedShort.valueOf(9),
              UnsignedShort.valueOf(10)},
          {UnsignedShort.valueOf(11), UnsignedShort.valueOf(12), UnsignedShort.valueOf(13), UnsignedShort.valueOf(14),
              UnsignedShort.valueOf(15)},
          {UnsignedShort.valueOf(16), UnsignedShort.valueOf(17), UnsignedShort.valueOf(18), UnsignedShort.valueOf(19),
              UnsignedShort.valueOf(20)},
          {UnsignedShort.valueOf(21), UnsignedShort.valueOf(22), UnsignedShort.valueOf(23), UnsignedShort.valueOf(24),
              UnsignedShort.valueOf(25)}}),

  UINT32_ARRAY("UInt32", Identifiers.UInt32, new UnsignedInteger[] {UnsignedInteger.valueOf(1),
      UnsignedInteger.valueOf(2), UnsignedInteger.valueOf(3), UnsignedInteger.valueOf(4), UnsignedInteger.valueOf(5)}),

  UINT32_MULTIDIMENSIONAL_ARRAY("UInt32", Identifiers.UInt32,
      new UnsignedInteger[][] {
          {UnsignedInteger.valueOf(1), UnsignedInteger.valueOf(2), UnsignedInteger.valueOf(3),
              UnsignedInteger.valueOf(4), UnsignedInteger.valueOf(5)},
          {UnsignedInteger.valueOf(6), UnsignedInteger.valueOf(7), UnsignedInteger.valueOf(8),
              UnsignedInteger.valueOf(9), UnsignedInteger.valueOf(10)},
          {UnsignedInteger.valueOf(11), UnsignedInteger.valueOf(12), UnsignedInteger.valueOf(13),
              UnsignedInteger.valueOf(14), UnsignedInteger.valueOf(15)},
          {UnsignedInteger.valueOf(16), UnsignedInteger.valueOf(17), UnsignedInteger.valueOf(18),
              UnsignedInteger.valueOf(19), UnsignedInteger.valueOf(20)},
          {UnsignedInteger.valueOf(21), UnsignedInteger.valueOf(22), UnsignedInteger.valueOf(23),
              UnsignedInteger.valueOf(24), UnsignedInteger.valueOf(25)}}),

  UINT64_ARRAY("UInt64", Identifiers.UInt64, new UnsignedLong[] {UnsignedLong.valueOf(1), UnsignedLong.valueOf(2),
      UnsignedLong.valueOf(3), UnsignedLong.valueOf(4), UnsignedLong.valueOf(5)}),

  UINT64_MULTIDIMENSIONAL_ARRAY("UInt64", Identifiers.UInt64,
      new UnsignedLong[][] {
          {UnsignedLong.valueOf(1), UnsignedLong.valueOf(2), UnsignedLong.valueOf(3), UnsignedLong.valueOf(4),
              UnsignedLong.valueOf(5)},
          {UnsignedLong.valueOf(6), UnsignedLong.valueOf(7), UnsignedLong.valueOf(8), UnsignedLong.valueOf(9),
              UnsignedLong.valueOf(10)},
          {UnsignedLong.valueOf(11), UnsignedLong.valueOf(12), UnsignedLong.valueOf(13), UnsignedLong.valueOf(14),
              UnsignedLong.valueOf(15)},
          {UnsignedLong.valueOf(16), UnsignedLong.valueOf(17), UnsignedLong.valueOf(18), UnsignedLong.valueOf(19),
              UnsignedLong.valueOf(20)},
          {UnsignedLong.valueOf(21), UnsignedLong.valueOf(22), UnsignedLong.valueOf(23), UnsignedLong.valueOf(24),
              UnsignedLong.valueOf(25)}}),

  XML_ELEMENT_ARRAY("XmlElement", Identifiers.XmlElement,
      new XmlElement[] {new XmlElement("<testElement1 />"), new XmlElement("<testElement2 />"),
          new XmlElement("<testElement3 />"), new XmlElement("<testElement4 />"), new XmlElement("<testElement5 />")}),

  XML_ELEMENT_MULTIDIMENSIONAL_ARRAY("XmlElement", Identifiers.XmlElement, new XmlElement[][] {
      {new XmlElement("<testElement1 />"), new XmlElement("<testElement2 />"), new XmlElement("<testElement3 />"),
          new XmlElement("<testElement4 />"), new XmlElement("<testElement5 />")},
      {new XmlElement("<testElement6 />"), new XmlElement("<testElement7 />"), new XmlElement("<testElement8 />"),
          new XmlElement("<testElement9 />"), new XmlElement("<testElement10 />")},
      {new XmlElement("<testElement11 />"), new XmlElement("<testElement12 />"), new XmlElement("<testElement13 />"),
          new XmlElement("<testElement14 />"), new XmlElement("<testElement15 />")},
      {new XmlElement("<testElement16 />"), new XmlElement("<testElement17 />"), new XmlElement("<testElement18 />"),
          new XmlElement("<testElement19 />"), new XmlElement("<testElement20 />")},
      {new XmlElement("<testElement21 />"), new XmlElement("<testElement22 />"), new XmlElement("<testElement23 />"),
          new XmlElement("<testElement24 />"), new XmlElement("<testElement25 />")}});

  public static final EnumSet<StaticData> DATA_ITEMS = EnumSet.of(BOOLEAN, BYTE, BYTE_STRING, DATE_TIME, DOUBLE, FLOAT,
      GUID, INT16, INT32, INT64, SBYTE, STRING, UINT16, UINT32, UINT64, XML_ELEMENT);

  public static final EnumSet<StaticData> STATIC_DATAS =
      EnumSet.of(VARIANT, BOOLEAN, BYTE, UINT16, UINT32, UINT64, SBYTE, INT16, INT32, INT64, FLOAT, DOUBLE, DURATION,
          STRING, BYTE_STRING, LOCALIZED_TEXT, QUALIFIED_NAME, GUID, NODE_ID, DATE_TIME, XML_ELEMENT, IMAGE, IMAGE_JPG,
          IMAGE_PNG, IMAGE_BMP, IMAGE_GIF, LOCALE_ID, NUMBER, INTEGER, UINTEGER, UTC_TIME, ENUMERATION);

  public static final EnumSet<StaticData> ANALOG_ARRAY_ITEMS = EnumSet.of(BOOLEAN_ARRAY, BYTE_ARRAY, UINT16_ARRAY,
      UINT32_ARRAY, UINT64_ARRAY, SBYTE_ARRAY, INT16_ARRAY, INT32_ARRAY, INT64_ARRAY, FLOAT_ARRAY, DOUBLE_ARRAY,
      DURATION_ARRAY, STRING_ARRAY, BYTE_STRING_ARRAY, DATA_TIME_ARRAY, GUID_ARRAY, XML_ELEMENT_ARRAY);

  public static final EnumSet<StaticData> STATIC_DATA_ARRAYS =
      EnumSet.of(VARIANT_ARRAY, BOOLEAN_ARRAY, BYTE_ARRAY, BYTE_STRING_ARRAY, DATA_TIME_ARRAY, DOUBLE_ARRAY,
          FLOAT_ARRAY, GUID_ARRAY, INT16_ARRAY, INT32_ARRAY, INT64_ARRAY, SBYTE_ARRAY, STRING_ARRAY,
          LOCALIZED_TEXT_ARRAY, QUALIFIED_NAME_ARRAY, UINT16_ARRAY, UINT32_ARRAY, UINT64_ARRAY, XML_ELEMENT_ARRAY);

  public static final EnumSet<StaticData> STATIC_DATA_MULTIDIMENSIONAL_ARRAYS =
      EnumSet.of(VARIANT_MULTIDIMENSIONAL_ARRAY, LOCALIZED_TEXT_MULTIDIMENSIONAL_ARRAY,
          QUALIZIED_NAME_MULTIDIMENSIONAL_ARRAY, BOOLEAN_MULTIDIMENSIONAL_ARRAY, BYTE_MULTIDIMENSIONAL_ARRAY,
          BYTE_STRING_MULTIDIMENSIONAL_ARRAY, DATE_TIME_MULTIDIMENSIONAL_ARRAY, DOUBLE_MULTIDIMENSIONAL_ARRAY,
          DURATION_MULTIDIMENSIONAL_ARRAY, FLOAT_MULTIDIMENSIONAL_ARRAY, GUID_MULTIDIMENSIONAL_ARRAY,
          INT16_MULTIDIMENSIONAL_ARRAY, INT32_MULTIDIMENSIONAL_ARRAY, INT64_MULTIDIMENSIONAL_ARRAY,
          SBYTE_MULTIDIMENSIONAL_ARRAY, STRING_MULTIDIMENSIONAL_ARRAY, UINT16_MULTIDIMENSIONAL_ARRAY,
          UINT32_MULTIDIMENSIONAL_ARRAY, UINT64_MULTIDIMENSIONAL_ARRAY, XML_ELEMENT_MULTIDIMENSIONAL_ARRAY);

  private String dataTypeName;
  private NodeId dataType;
  private Object initialValue;

  private StaticData(String dataTypeName, NodeId dataType, Object initialValue) {
    this.dataTypeName = dataTypeName;
    this.dataType = dataType;
    this.initialValue = initialValue;
  }

  @Override
  public String getBaseName() {
    return dataTypeName;
  }

  @Override
  public NodeId getDataTypeId() {
    return dataType;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  @Override
  public Object getInitialValue() {
    return initialValue;
  }

}
